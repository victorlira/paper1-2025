# RefFilter Replication Package

This repository contains the results and replication packages for the paper **"RefFilter: Improving Semantic Conflict Detection via Refactoring-Aware Static Analysis"**.

## Results

All experiment outputs are provided in the `results` directory:

* **experiment-1.tar.xz**: Detailed results for **Experiment 1 – Performance Comparison with Existing Methods**.
* **experiment-2.tar.xz**: Detailed results for **Experiment 2 – Scaling and Diversity Evaluation**.

Each archive includes:

1. **summary\_experiment.csv**: A CSV file summarizing the overall results of the experiment.
2. **scenarios/**: A folder containing per-scenario subdirectories. In each scenario folder, you will find:

   * `outConsole.txt`: Log from the pure static analysis tool.
   * `refactor-result.json`: Detailed RefFilter outputs regarding reported refactorings.
   * `refactoring_interferences.json`: Interferences labeled as false positives due to refactorings.
   * `no_refactoring_interferences.json`: Interferences not attributed to refactorings.

## Datasets

The two datasets used in our experiments can be accessed at the following anonymous URLs:

* **Experiment 1 dataset**: [https://anonymous.4open.science/r/mergedataset-9828/](https://anonymous.4open.science/r/mergedataset-9828/)
* **Experiment 2 dataset**: [https://anonymous.4open.science/r/ref-dataset-974E/](https://anonymous.4open.science/r/ref-dataset-974E/)

## Replication

This section explains how to reproduce all experiments using Docker.

### Prerequisites

* **Docker** installed on your machine. Follow the instructions at [https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/).
* Clone this repository to your local machine.

### Available Experiment Scripts

* **`run_experiment_small.sh`**: Quick test run for a small number of scenarios to validate the pipeline.
* **`run_experiment_1.sh`**: Executes **Experiment 1 – Performance Comparison with Existing Methods**.
* **`run_experiment_2.sh`**: Executes **Experiment 2 – Scaling and Diversity Evaluation**.

### Build the Docker Image

From the root of this repository:

```bash
docker build -t refilter-experiments:latest .
```

This image includes Java 8 (default), Java 21, Git, Python 3, and all required dependencies.

### Running Experiments

Each experiment writes its output under `/workspace/experiment-*/` inside the container. We then copy that folder to a host directory.

#### 1. Small Experiment

```bash
# Prepare local output folder
rm -rf results-small && mkdir results-small

# Run container (default script: run_experiment_small.sh)
docker run --name exp-small refilter-experiments:latest \
  ./run_experiment_small.sh

# Copy results to host
docker cp exp-small:/workspace/experiment-small ./results-small

docker rm exp-small

# Inspect results
ls results-small
head results-small/results-with-build-information.csv
```

#### 2. Experiment 1

```bash
# Prepare local output folder
rm -rf results-1 && mkdir results-1

# Run container overriding default command
docker run --name exp-1 refilter-experiments:latest \
  ./run_experiment_1.sh

# Copy results to host
docker cp exp-1:/workspace/experiment-1 ./results-1

docker rm exp-1

# Inspect results
ls results-1
head results-1/results-with-build-information.csv
```

#### 3. Experiment 2

```bash
# Prepare local output folder
rm -rf results-2 && mkdir results-2

# Run container overriding default command
docker run --name exp-2 refilter-experiments:latest \
  ./run_experiment_2.sh

# Copy results to host
docker cp exp-2:/workspace/experiment-2 ./results-2

docker rm exp-2

# Inspect results
ls results-2
head results-2/results-with-build-information.csv
```

---

For any questions or issues, please refer to the paper or open an issue on this repository.
