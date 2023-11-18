service = readiness

ROOT_DIR:=$(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))

define checkstyle
#!/bin/sh
make style
exit $?
endef
export checkstyle

.PHONY: setup
setup:
	@cd $(ROOT_DIR) && \
echo "$$checkstyle" > .git/hooks/pre-push && chmod 755 .git/hooks/pre-push && echo "setup completed"

.PHONY: style
style:
	@cd $(ROOT_DIR) && \
./mvnw --no-transfer-progress checkstyle:check

.PHONY: version
version:
	@cd $(ROOT_DIR) && \
VERSION="$$(./mvnw org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout)" && \
echo "$${VERSION}"

.PHONY: clean
clean:
	@cd $(ROOT_DIR) && \
./mvnw --no-transfer-progress clean

.PHONY: test
test: clean
	@cd $(ROOT_DIR) && \
./mvnw --no-transfer-progress test

.PHONY: package
package: clean
	@cd $(ROOT_DIR) && \
./mvnw --no-transfer-progress package

.PHONY: build
build:
	@cd $(ROOT_DIR) && \
IMGVER="$$(./mvnw org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout)" && \
docker build \
  -t paolodenti/${service}:$${IMGVER} \
  -f .docker/Dockerfile .
