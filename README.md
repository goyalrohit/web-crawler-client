# Web Crawler Client

This is a Java-based client application that interacts with a web crawler service to generate sitemaps for websites. The client listens for messages from an ActiveMQ queue and triggers the sitemap generation process.

Sequence Diagram : 

Client                  WebCrawlerService                  WebCrawler
  |                             |                              |
  | Open an HTTP Connection     |                              |
  |------------------------------>                             |
  |                             |                              |
  | Request Crawl               |                              |
  |------------------------------>                             |
  |                             | Create WebCrawler Instance   |
  |                             |------------------------------>
  |                             |                              |
  |                             | Start Crawling               |
  |                             |<------------------------------>
  |                             |                              |
  |                             | Fetch Data                   |
  |                             |<------------------------------>
  |                             |                              |
  | Active MQ Data Transfer     |                              |
  |<-------------------------------                             |
  |                             |                              |
  |                             | Fetch Data                   |
  |                             |<------------------------------>
  |                             |                              |
  | Active MQ Data Transfer     |                              |
  |<-------------------------------                             |
  |                             |                              |
  |                             | Stop Crawling                |
  |                             |<------------------------------>
  |                             |                              |
  | Close HTTP  Connection      |                              |
  |<-------------------------------                            |
  |                             |                              |

## Prerequisites

- Java 8 or higher
- Apache Maven
- Apache ActiveMQ (message broker)

## Installation

1. Clone the repository:

git clone https://github.com/goyalrohit/web-crawler-client.git


2. Navigate to the project directory:

cd web-crawler-client


3. Build the project using Maven:

mvn clean install


## Configuration

1. Verify the `application.yml` file with the appropriate ActiveMQ configuration:

activemq.broker-url=tcp://localhost:61616
activemq.destination.subscriber.crawler=crawlertopic

 OR 

activemq:
  broker-url: tcp://localhost:61616
  user: admin
  password: admin
  destination:
    subscriber:
      crawler: crawlertopic

If you configure differnet credentials for ActiveMQ. Please configure the user and password accordingly.

## Usage

1. Download ActiveMQ message broker. https://activemq.apache.org/components/classic/documentation/download-archives

2. Start the ActiveMQ message broker.

3. Navigate to the the Active MQ Local URL : http://127.0.0.1:8161/admin/queues.jsp

4. Make sure the server is setup to communicate with the client -

https://github.com/goyalrohit/web-crawler-service

5. Run the client application:

mvn spring-boot:run

3. Send a request to the WebCrawlerServer with the URL of the website you want to crawl.

4. For example: Hit the URL in Postman or any webBrowser - http://localhost:8082/sitemap?url=http://www.redhat.com

5. The client will receive the message, trigger the sitemap generation process, and create a sitemap file for the specified website.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).

## Contact

For any questions or support, please contact me (project maintainer) at [irohitgoyal@gmail.com].



