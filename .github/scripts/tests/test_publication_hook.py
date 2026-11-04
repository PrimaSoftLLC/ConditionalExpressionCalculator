import json
import subprocess
import sys
import unittest
from pathlib import Path

HOOK = Path(__file__).resolve().parents[3] / '.Codex/hooks/guard_command.py'


class HookTests(unittest.TestCase):
    def test_build_allowed_and_quoted_publication_blocked(self):
        cases = {'mise exec -- mvn verify': 0, './mvnw install': 0,
                 'mise exec -- mvn deploy': 2, "mise exec -- mvn 'deploy'": 2,
                 'mise exec -- mvn "deploy"': 2,
                 './mvnw org.apache.maven.plugins:maven-deploy-plugin:3.1.4:deploy-file': 2,
                 'docker push example/image': 2, './mvnw docker:push': 2}
        for command, expected in cases.items():
            with self.subTest(command=command):
                result = subprocess.run([sys.executable, str(HOOK)],
                                        input=json.dumps({'tool_input': {'command': command}}),
                                        text=True, capture_output=True)
                self.assertEqual(expected, result.returncode, result.stderr)


if __name__ == '__main__':
    unittest.main()
