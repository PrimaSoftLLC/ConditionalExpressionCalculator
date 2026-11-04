import sys
import subprocess
import os
import unittest
from pathlib import Path
from tempfile import TemporaryDirectory
from unittest.mock import patch
from zipfile import ZipFile

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import release_checks as rc


class ReleaseTests(unittest.TestCase):
    def test_valid_release_with_crlf_and_bom(self):
        rc.validate_release('7.5', '\ufeff# Changes\r\n## 7.5\r\n\r\n- Packages\r\n')

    def test_invalid_versions_and_changelog(self):
        for version in ['7.5-SNAPSHOT', '7.5-DEV', '', '7.5;echo bad', '7']:
            with self.subTest(version=version), self.assertRaises(rc.ReleaseError):
                rc.validate_release(version, '## ' + version + '\n- note')
        for text in ['', '## 7.5\n', '## 7.5\n## 7.4\n- old',
                     '## 7.5\n- x\n## 7.5\n- y', '## Не выпущено\n\n## 7.5\n- x']:
            with self.subTest(text=text), self.assertRaises(rc.ReleaseError):
                rc.validate_release('7.5', text)

    def remote(self, responses):
        with patch.object(rc, 'request', side_effect=responses):
            rc.check_remote_release('PrimaSoftLLC/ConditionalExpressionCalculator',
                                    'by.aurorasoft', 'conditional-expression-calculator',
                                    '7.5', 'actor', 'SECRET')

    def test_missing_version_with_authenticated_access(self):
        self.remote([(200, b'{}'), (404, b''), (404, b''),
                     (404, b''), (404, b''), (404, b'')])

    def test_existing_tags_or_partial_artifact(self):
        for index in range(1, 6):
            responses = [(200, b'{}')] + [(404, b'')] * 5
            responses[index] = (200, b'<metadata><versioning><versions><version>7.5</version></versions></versioning></metadata>')
            with self.subTest(index=index), self.assertRaises(rc.ReleaseError):
                self.remote(responses)

    def test_access_network_and_malformed_metadata_fail_closed(self):
        for status in [401, 403, 429, 500, 302]:
            with self.subTest(status=status), self.assertRaises(rc.ReleaseError):
                self.remote([(status, b'')])
            with self.subTest(registry_status=status), self.assertRaises(rc.ReleaseError):
                self.remote([(200, b'{}'), (404, b''), (404, b''), (status, b'')])
        with self.assertRaises(rc.ReleaseError):
            self.remote([(200, b'{}'), (404, b''), (404, b''), (200, b'not XML')])
        with patch.object(rc.urllib.request.OpenerDirector, 'open', side_effect=TimeoutError('SECRET')):
            with self.assertRaises(rc.ReleaseError) as failure:
                rc.request('https://api.github.com/repos/x/y', 'SECRET')
            self.assertNotIn('SECRET', str(failure.exception))

    def test_cli_blocks_unreleased_changelog_before_network(self):
        root = Path(__file__).resolve().parents[3]
        with TemporaryDirectory() as tmp:
            pom = Path(tmp) / 'pom.xml'
            changelog = Path(tmp) / 'CHANGELOG.md'
            pom.write_text('<project><groupId>by.aurorasoft</groupId><artifactId>conditional-expression-calculator</artifactId><version>7.5</version></project>')
            changelog.write_text('## Не выпущено\n- Pending release\n', encoding='utf-8')
            result = subprocess.run([sys.executable, str(root / '.github/scripts/release_checks.py'),
                                     'preflight', '--pom', str(pom), '--changelog', str(changelog)],
                                    capture_output=True, text=True,
                                    env={**os.environ, 'GITHUB_TOKEN': 'SECRET'})
        self.assertEqual(1, result.returncode)
        self.assertIn('changelog', result.stdout)
        self.assertNotIn('SECRET', result.stdout + result.stderr)

    def test_artifact_identity_and_manifest_continuations(self):
        with TemporaryDirectory() as tmp:
            root = Path(tmp)
            pom = root / 'pom.xml'
            pom.write_text('<project><groupId>by.aurorasoft</groupId><artifactId>conditional-expression-calculator</artifactId><version>7.5</version></project>')
            jar = root / 'local.jar'
            sha = 'a' * 40
            with ZipFile(jar, 'w') as z:
                z.writestr('META-INF/MANIFEST.MF', 'Implementation-Version: 7.5\r\nBuild-Revision: ' + sha[:20] + '\r\n ' + sha[20:] + '\r\n')
            fetched = root / 'fetched.jar'
            fetched.write_bytes(jar.read_bytes())
            rc.verify_download(pom, jar, pom, fetched, sha)
            for expected in ['b' * 40, '${git.commit.id.full}']:
                with self.assertRaises(rc.ReleaseError):
                    rc.verify_download(pom, jar, pom, fetched, expected)
            fetched.write_bytes(b'different')
            with self.assertRaises(rc.ReleaseError):
                rc.verify_download(pom, jar, pom, fetched, sha)


if __name__ == '__main__':
    unittest.main()
