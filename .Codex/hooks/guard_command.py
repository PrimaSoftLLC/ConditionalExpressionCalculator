"""Optional Bash PreToolUse guard: inspect input, never execute its command."""
import json
import re
import shlex
import sys

try:
    command = json.load(sys.stdin)['tool_input']['command']
    lexer = shlex.shlex(command, posix=True, punctuation_chars=True)
    lexer.whitespace_split = True
    tokens = list(lexer)
    blocked = any(re.search(r'(^|:)deploy(-file)?(:|$)', token) or 'docker:push' in token for token in tokens)
    blocked |= any(token == 'docker' and tokens[i + 1:i + 2] == ['push'] for i, token in enumerate(tokens))
except (ValueError, KeyError, TypeError):
    blocked = True
if blocked:
    print('Publishing is reserved for release CI. See readme.md: Release flow.', file=sys.stderr)
    sys.exit(2)
