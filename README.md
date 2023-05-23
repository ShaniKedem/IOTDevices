#IOT-Platform
IOT-Platform aims to create a comprehensive IOT infrastructure for companies and their products, providing features such as updates, alerts, statistical calculations, proactive maintenance, and optimal device performance. The platform is designed using object-oriented principles, SOLID principles, and incorporates various design patterns for a robust and scalable system architecture.

##Getting Started
To get started with the IoT Platform, follow these steps:

Clone the repository: git clone https://github.com/LinWeintraub/iot-platform.git
Set up the required dependencies, including the web server, MySQL database and MongoDB database.
Configure the platform settings, such as the database connection details, in the appropriate configuration files.
Build and run the project.

##Features
###Company and Product Registration: The platform allows companies to register themselves and their products. Company and product details are stored in a MySQL database, enabling seamless management of registered entities.

###User Registration and Updates: Customers who purchase IoT products from registered companies can register with their individual products. They will receive updates and notifications regarding their products, which are stored by the platform for future reference. The details are stored in a MongoDB database.

JSON Request Handling & Thread Pool: User registration information is submitted to the platform as a JSON request. The requests are efficiently handled by dedicated threads from a thread pool, ensuring concurrent processing and responsiveness.

###Singleton Command Factory: The Singleton Command Factory serves as a central repository for storing and executing functions associated with various operations triggered by JSON requests. It allows for a flexible architecture, enabling the addition of new features by dynamically loading additional JAR files. This extensible approach empowers companies to seamlessly integrate new functionalities into the platform.

###Dir Monitor: The Dir Monitor module utilizes the Observer design pattern to actively monitor a designated folder. This folder allows companies to add JAR files containing additional commands as needed. When a new JAR file is added to the monitored folder, it is automatically detected and loaded into the Singleton Command Factory, enabling the execution of newly added commands without any manual configuration.
