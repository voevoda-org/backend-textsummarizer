# Backend Text Summarizer

This project is providing the backend for the textSummarizer app available on mobile platforms.

## Table of Contents

1. [Installation](#installation)
2. [Running the app](#running-the-app)
2. [Usage](#usage)
3. [License](#license)

<a name="installation"></a>

## Installation

To customize the app's parameters like the secret included in the JWT token,
you have to set the environment parameters accordingly.
You can find those in the application.conf file.
After that, you can build the app via gradle and run it locally. 

<a name="usage"></a>

## Running the app

To run the app locally, you need to have docker installed and run the following command to start a db container:

```bash
docker run --name textsummarizer-db -e POSTGRES_PASSWORD=secret -e POSTGRES_USER=postgres -e POSTGRES_DB=textsummarizer -p 5432:5432 -d postgres
```

Make sure to adjust the variables in your application.conf file accordingly.

## Usage

For information on how to interact with the backend,
have a look at the provided openapi specification or take a look at the tests.

<a name="license"></a>

## License

This project is licensed under Apache-2.0.