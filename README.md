# security-v1 demo

Demo bookstore application with GitHub Advanced Security features and Azure deployments.

This is a simple Maven project that builds a standalone JAR which contains a Jetty webserver and some Java Servlets. The application is able
to be built into a container and then available to be deployed as an Azure Web App.

The application is intentionally full of alerts, so that we can talk about the customer process of adding GHAS functionality to an application and then what the next steps would be to understand the findings and how to remediate those findings.

For a step-by-step guide see the [guide](./docs/README.md) in the repository.

![bookstore](https://user-images.githubusercontent.com/681306/114581130-5e2d4b00-9c77-11eb-837b-4efaefa29e39.png)



### GitHub Codespaces

This repository is configured with GitHub Codespaces to make it easy to start with a development environment fully configured.

The container used for the development environment is available from https://github.com/octodemo/container-java-development and is publically available.
There are multiple versions of this, all with the tags providing a specific combination of tools for various cloud vendors. By default you will get a
container with:
* Maven 3.6.3 or later
* JDK 11
* Azure CLI tools



### Running in a Docker container:

The Codespace is configured to build and execute the container as a tasks.

* `docker: build container` will build the java project and then the container for you, prompting for details along the way
* `docker: run container` will allow you to run the container that you built allowing you to select the port that is bound to (`8080` by default).

Building and running the container locally without the tasks in GitHub Codespaces can be done using the following;

* `mvn package`
* `docker build . --build-arg VERSION=1.0.0-SNAPSHOT --tag security-v1:latest` (update to the correct version that mvn package will build for you)
* `docker run -p 8080:8080 security-v1:latest` to execute the container and bind to port `8080` to serve requests from
