#!/bin/bash

DOT_GIT_DIR=$(git rev-parse --git-dir)
REPO_DIR=$(git rev-parse --show-toplevel)

echo "Installing pre-commit hook"
cp "${REPO_DIR}/tools/git-hook/pre-commit" "${DOT_GIT_DIR}/hooks/pre-commit" && \
  chmod +x "${DOT_GIT_DIR}/hooks/pre-commit"
