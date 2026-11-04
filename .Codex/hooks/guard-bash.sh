#!/usr/bin/env bash
# Optional PreToolUse Bash hook. This file does not grant or change CI permissions.
hook_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
if command -v python3 >/dev/null 2>&1; then
    exec python3 "$hook_dir/guard_command.py"
fi
exec python "$hook_dir/guard_command.py"
