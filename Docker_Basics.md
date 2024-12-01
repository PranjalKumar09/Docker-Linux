# Docker Basics
====================

Docker makes it easier for testers to run and test applications developed by software engineers. It simplifies the deployment process by providing a consistent environment that can be replicated across different systems.

## What is a Docker Container?
-------------------------------

A Docker container is an isolated environment that can run an application and its dependencies in a standardized way. It allows for:

- Packaging an application with all its necessary dependencies and configuration files.
- Easy sharing and portability across different systems.
- Efficient development and deployment workflows.

### Key Points about Containers:
- For each Docker image, multiple containers can be created.
- Containers are lightweight and share the host system's OS kernel.

## Docker Architecture
----------------------

The architecture of Docker consists of the following layers:

- **Hardware** -> **Operating System (OS)** -> **Docker Engine** -> **Container(s)**

Multiple Docker containers can run simultaneously on the same host system. For example:

- Hardware -> OS -> Docker Engine -> Container (v1) version 1
                                      -> Container (v2) version 2

## Docker Containers vs Virtual Machines (VMs)
-----------------------------------------------

Here’s a comparison between Docker containers and Virtual Machines (VMs):

| **Feature**           | **Docker Containers**             | **VMs**                        |
|-----------------------|-----------------------------------|--------------------------------|
| **OS Impact**         | Low impact on the host OS         | High impact on the host OS     |
| **Performance**       | Very fast, minimal resource usage | Slower due to overhead         |
| **Disk Space**        | Low disk space usage             | High disk space usage         |
| **Encapsulation**     | Encapsulates applications only    | Encapsulates an entire machine |
| **OS**                | Does not require its own OS       | Each VM runs its own OS        |
| **Virtualization**    | Uses OS-level virtualization      | Uses hypervisor for full virtualization |

### Docker Benefits:
- Containers are easier to rebuild, distribute, and share.
- Docker containers are more lightweight and efficient compared to VMs.

## Main Components of Docker
-----------------------------

The core components of Docker are:

1. **Dockerfile**: A script that defines the steps to create a Docker image.
2. **Docker Image**: A read-only template used to create containers.
3. **Docker Container**: A running instance of a Docker image.
4. **Docker Registry**: A centralized repository for storing and distributing Docker images.

### Important Note:
- **Dockerfile** does not have an extension; it is simply named `Dockerfile`.

## Docker Registry
------------------

A **Docker Registry** is a centralized location for storing and distributing Docker images. **Docker Hub** is the default public registry.

- A **repository** in Docker Hub stores related images, often differentiated by tags (e.g., `v1`, `latest`).

## Common Docker Commands
-------------------------

### Docker Run
To run a Docker container:

```bash
docker run -p host_port:container_port <image-id>
```

- `-p`: Maps the container's internal port to the host machine's port.
- **host_port**: The port on the host machine.
- **container_port**: The port inside the Docker container.

### Detached Mode
To run a container in the background (detached mode):

```bash
docker run -d <image-id>
```

- `-d`: Runs the container in the background and returns control to the command prompt immediately.

### Avoiding Port Conflicts
- You cannot bind the same host port to multiple Docker containers simultaneously. This will result in a port conflict error.

## Pushing and Pulling Images from Docker Hub
-------------------------------------------

### Pushing an Image to Docker Hub:
1. **Build your Docker image** with a tag (e.g., `v1`):
   ```bash
   docker build -t <username>/<repo-name>:v1 .
   ```

2. **Push the image** to Docker Hub:
   ```bash
   docker push <username>/<repo-name>:v1
   ```

- You need to log in using `docker login` before pushing images.

### Pulling an Image from Docker Hub:
To pull an image from Docker Hub:

```bash
docker pull <username>/<repo-name>:v1
```

## Docker Volume
----------------

Docker volumes allow data to persist even when containers are stopped or removed. For example, if you run a container with a Python program that interacts with files, you can create a volume to retain those changes:

```bash
docker run -it --rm -v myvolume:/myapp/ <image-id>
```

This ensures that changes to files in `/myapp/` are saved outside the container, in the volume.

### Help Command
To get help on Docker commands:

```bash
docker <command> --help
```

## Mount Binds
--------------

**Mount binds** allow you to map a file or directory from the host to the container. This is useful when you want to see real-time changes in the files while running the container. For example:

```bash
docker run -v "/path/on/host:/path/in/container" --rm <image-id>
```

Changes made to the file on the host will be reflected inside the container.

### Dockerignore
---------------

A `.dockerignore` file is used to exclude files and directories from being included in the Docker image. It works similarly to `.gitignore`. For example, you can add files like `Dockerfile` in `.dockerignore` if you don't want them included in the image.

## Working with APIs in Docker Containers
----------------------------------------

When importing an external module, such as `requests` in Python, it may not be available in the Docker container. To install missing dependencies, use the following command:

```bash
RUN pip install requests
```

Then, your application will be able to access the external dependencies.

## Containers with Local Databases
----------------------------------

When running a containerized application that connects to a local database, you may encounter errors due to networking issues. For example, if your application is trying to connect to a database using `localhost`, you should modify it to connect to the Docker host:

```python
host = "host.docker.internal"
```

This allows the container to communicate with the database running on the host system.

## Docker Networks
------------------

Docker networks provide a way for containers to communicate with each other. To specify a network when running a container, use the `--network` flag:

```bash
docker run --network <network-name> <image-id>
```

Docker provides several types of network drivers:
- **host**
- **bridge** (default)
- **user-defined bridge** (custom)
- **none**
- **MACVLAN**
- **IPVLAN**
- **Overlay**

## Docker Compose
-----------------

**Docker Compose** is a tool that allows you to define and manage multi-container Docker applications using a `docker-compose.yml` file. It simplifies running and managing multiple containers by defining the configuration in a single file.

### Example of Docker Compose Usage:
To define a multi-container application with Docker Compose:

```yaml
version: '3'
services:
  web:
    image: webapp
    ports:
      - "5000:5000"
  db:
    image: mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
```

To start the application:

```bash
docker-compose up
```

### Running Containers in Detached Mode:
To run containers in detached mode:

```bash
docker-compose up -d
```

### Docker Compose with Dependencies
You can define container dependencies with the `depends_on` directive, ensuring that containers start in a specific order.

### Volumes in Docker Compose:
You can also define volumes in `docker-compose.yml` to persist data across containers:

```yaml
volumes:
  - ./data:/data
```

## Multi-Stage Docker Builds
-----------------------------

Multi-stage builds allow you to optimize Docker images by dividing the build process into multiple stages. This way, you can separate the build environment (with all dependencies) from the runtime environment, reducing the final image size.

### Example of a Multi-Stage Dockerfile:

```dockerfile
# Stage 1: Build the application
FROM python:3.7 AS builder
WORKDIR /app
COPY . .
RUN pip install -r requirements.txt

# Stage 2: Create a minimal runtime image
FROM python:3.7-slim
WORKDIR /app
COPY --from=builder /usr/local/lib/python3.7/site-packages/ .
COPY . .
ENTRYPOINT ["python", "run.py"]
```

In a multi-stage Docker build, only the final image is tagged and used. The intermediate stages (e.g., `builder`) are discarded once the build is complete.

## Docker in Production
------------------------

While Docker is widely used in development and testing environments, it is not typically used for production deployment on its own. For production-grade container orchestration, **Kubernetes** is commonly used. Kubernetes builds on Docker and handles traffic scaling, failure recovery, and other challenges that Docker alone cannot efficiently manage.
