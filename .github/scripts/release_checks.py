"""Read-only release validation. This module never publishes or creates tags."""
import argparse
import base64
import hashlib
import os
import re
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET
from pathlib import Path
from zipfile import ZipFile, BadZipFile


class ReleaseError(Exception):
    pass


def validate_release(version, changelog):
    if not re.fullmatch(r'\d+\.\d+(?:\.\d+)?', version):
        raise ReleaseError('Release version must be X.Y or X.Y.Z, without qualifiers')
    lines = changelog.lstrip('\ufeff').splitlines()
    headers = [line.strip() for line in lines if line.startswith('## ')]
    target = '## ' + version
    if '## Не выпущено' in headers or headers.count(target) != 1:
        raise ReleaseError('Close the unreleased changelog into exactly one release section')
    start = next(i for i, line in enumerate(lines) if line.strip() == target)
    notes = []
    for line in lines[start + 1:]:
        if line.startswith('## '):
            break
        if line.strip() and not line.startswith('#'):
            notes.append(line)
    if not notes:
        raise ReleaseError('Release changelog section is empty')


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def request(url, authorization):
    req = urllib.request.Request(url, headers={'Authorization': authorization,
                                              'User-Agent': 'calculator-release-check'})
    try:
        with urllib.request.build_opener(NoRedirect).open(req, timeout=30) as response:
            return response.status, response.read()
    except urllib.error.HTTPError as error:
        error.close()
        return error.code, b''
    except (OSError, urllib.error.URLError):
        raise ReleaseError('Network request failed; release is blocked') from None


def check_remote_release(repository, group, artifact, version, actor, token):
    api_auth = 'Bearer ' + token
    api = 'https://api.github.com/repos/' + repository
    status, _ = request(api, api_auth)
    if status != 200:
        raise ReleaseError('Cannot confirm authenticated repository access: HTTP ' + str(status))
    for tag in ['v' + version, version]:
        status, _ = request(api + '/git/ref/tags/' + tag, api_auth)
        if status != 404:
            raise ReleaseError('Tag exists or tag check failed: ' + tag + ' HTTP ' + str(status))
    auth = 'Basic ' + base64.b64encode((actor + ':' + token).encode()).decode()
    base = 'https://maven.pkg.github.com/' + repository.lower() + '/' + group.replace('.', '/') + '/' + artifact
    status, body = request(base + '/maven-metadata.xml', auth)
    if status == 200:
        try:
            metadata = ET.fromstring(body)
            if metadata.tag != 'metadata':
                raise ValueError()
            if version in [e.text for e in metadata.findall('./versioning/versions/version')]:
                raise ReleaseError('Maven version already exists')
        except (ET.ParseError, ValueError):
            raise ReleaseError('Invalid Maven metadata') from None
    elif status != 404:
        raise ReleaseError('Registry check failed: HTTP ' + str(status))
    for extension in ['pom', 'jar']:
        status, _ = request(f'{base}/{version}/{artifact}-{version}.{extension}', auth)
        if status != 404:
            raise ReleaseError('Artifact exists or registry check failed: ' + extension + ' HTTP ' + str(status))


def coordinates(pom):
    root = ET.parse(pom).getroot()
    ns = {'m': 'http://maven.apache.org/POM/4.0.0'} if root.tag.startswith('{') else {}
    prefix = 'm:' if ns else ''
    values = tuple(root.findtext(prefix + key, namespaces=ns) for key in ['groupId', 'artifactId', 'version'])
    if values[:2] != ('by.aurorasoft', 'conditional-expression-calculator') or not values[2]:
        raise ReleaseError('Unexpected Maven coordinates')
    return values


def verify_download(local_pom, local_jar, fetched_pom, fetched_jar, expected_sha):
    gav = coordinates(local_pom)
    if coordinates(fetched_pom) != gav:
        raise ReleaseError('Downloaded coordinates differ')
    for local, fetched in [(local_pom, fetched_pom), (local_jar, fetched_jar)]:
        if hashlib.sha256(local.read_bytes()).digest() != hashlib.sha256(fetched.read_bytes()).digest():
            raise ReleaseError('Downloaded artifact differs from verified candidate')
    with ZipFile(fetched_jar) as jar:
        manifest = jar.read('META-INF/MANIFEST.MF').decode().replace('\r\n', '\n').replace('\n ', '')
    fields = dict(line.split(': ', 1) for line in manifest.splitlines() if ': ' in line)
    if not re.fullmatch('[0-9a-f]{40}', expected_sha) or fields.get('Build-Revision') != expected_sha or fields.get('Implementation-Version') != gav[2]:
        raise ReleaseError('Artifact manifest version/revision mismatch')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    subs = parser.add_subparsers(dest='command', required=True)
    pre = subs.add_parser('preflight')
    pre.add_argument('--pom', type=Path, required=True)
    pre.add_argument('--changelog', type=Path, required=True)
    verify = subs.add_parser('verify-download')
    for name in ['local-pom', 'local-jar', 'fetched-pom', 'fetched-jar']:
        verify.add_argument('--' + name, type=Path, required=True)
    args = parser.parse_args()
    try:
        if args.command == 'preflight':
            group, artifact, version = coordinates(args.pom)
            validate_release(version, args.changelog.read_text(encoding='utf-8-sig'))
            check_remote_release(os.environ['GITHUB_REPOSITORY'], group, artifact, version,
                                 os.environ['GITHUB_ACTOR'], os.environ['GITHUB_TOKEN'])
        else:
            verify_download(args.local_pom, args.local_jar, args.fetched_pom, args.fetched_jar, os.environ['GITHUB_SHA'])
    except (ReleaseError, OSError, KeyError, ET.ParseError, BadZipFile) as error:
        # Never include transport exception text (it may contain credentials).
        print('Release blocked: ' + (str(error) if isinstance(error, ReleaseError) else type(error).__name__))
        return 1
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
