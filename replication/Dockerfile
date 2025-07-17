# ────────────────────────────────────────────────────────────────
# Dockerfile for running any of: run_experiment_small.sh,
#                               run_experiment_1.sh,
#                               run_experiment_2.sh
# ────────────────────────────────────────────────────────────────
FROM ubuntu:22.04

# 1) Install utilities, Git, Python and JDKs
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      ca-certificates curl unzip python3 dos2unix git \
      gnupg dirmngr software-properties-common \
      openjdk-8-jdk \
 && add-apt-repository ppa:openjdk-r/ppa -y \
 && apt-get update \
 && apt-get install -y --no-install-recommends openjdk-21-jdk \
 && rm -rf /var/lib/apt/lists/*

# 2) Configure Java 8 as default, Java 21 as alternative
RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-8-openjdk-amd64/bin/java 1081 \
 && update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-21-openjdk-amd64/bin/java 2111 \
 && update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/bin/java

# 3) Configure Git (so commits inside container “just work”)
RUN git config --global user.name  "user" \
 && git config --global user.email "user@example.com"

# 4) Set workdir and copy your entire project into it
WORKDIR /workspace
COPY . /workspace

# 5) Normalize line endings & make your scripts + gradlew executable
RUN dos2unix run_experiment_small.sh run_experiment_1.sh run_experiment_2.sh get-dataset.sh gradlew 2>/dev/null || true \
 && chmod +x run_experiment_small.sh run_experiment_1.sh run_experiment_2.sh get-dataset.sh gradlew

# 6) No default CMD: we'll specify which script to run at `docker run`
