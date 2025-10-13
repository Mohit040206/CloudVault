🗂️ CloudVault

CloudVault is a secure and lightweight personal cloud storage platform that allows a single user to upload, view, download, search, and delete files from anywhere.
Built with Spring Boot, MySQL, and Thymeleaf, CloudVault is designed to be simple yet scalable, serving as a foundation for future multi-user or enterprise expansion.

🚀 Features

✅ User Authentication – Secure login and registration with password encryption.
✅ File Upload – Upload files of any type (PDF, images, documents, etc.).
✅ File Management – View, download, and delete uploaded files.
✅ Search Functionality – Search your files instantly by name.
✅ Session Handling – Auto logout after inactivity (for security).
✅ Responsive UI – Simple HTML + CSS interface, no JavaScript dependencies.

🧩 Tech Stack
Layer	Technology
Backend	Spring Boot 3.x, Java 21
Frontend	HTML, CSS, Thymeleaf
Database	MySQL
Security	Spring Security (custom login endpoints)
Build Tool	Maven
🗃️ Project Structure
cloudvault/
│
├── src/
│   ├── main/
│   │   ├── java/com/cloudvault/
│   │   │   ├── configuration/     # Security & App Configs
│   │   │   ├── controller/        # Handles UI routes and requests
│   │   │   ├── entity/            # JPA entities (Document, User)
│   │   │   ├── repository/        # Repositories for database interaction
│   │   │   ├── service/           # Business logic (DocumentService, UserService)
│   │   │   └── model/             # Helper models
│   │   ├── resources/
│   │   │   ├── templates/         # HTML pages (login, home, gallery, etc.)
│   │   │   ├── static/            # CSS, icons, uploads
│   │   │   └── application.properties
│   └── test/
│
├── uploads/                       # Directory where uploaded files are stored
├── pom.xml                        # Maven dependencies
└── README.md                      # You are here 😄

⚙️ Configuration

Before running, configure your MySQL credentials inside
src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/cloudvault
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

server.port=8080

▶️ Run the Project
# 1️⃣ Clone the repository
git clone https://github.com/Mohit040206/CloudVault.git
cd CloudVault

# 2️⃣ Build the project
mvn clean install

# 3️⃣ Run the Spring Boot app
mvn spring-boot:run


Visit: http://localhost:8080

🧠 Key Endpoints
Endpoint	Method	Description
/login	GET/POST	User login
/upload	POST	Upload a file
/gallery	GET	View all files
/download/{id}	GET	Download by file ID
/delete/{id}	DELETE	Delete file
/search	GET	Search user’s uploaded files
📂 Upload Directory

All uploaded files are saved in:

uploads/


Each file is renamed using a timestamp and UUID to prevent duplication.

🔒 Security Notes

Default Spring Security login is disabled for custom endpoints.


Passwords are encrypted using BCryptPasswordEncoder.

Session expires automatically after 20 minutes of inactivity.

📸 Screenshots
Home Page
<img width="1897" height="865" alt="image" src="https://github.com/user-attachments/assets/ae5e10cc-dd59-4b41-9d91-f08e88845c5f" />
About Page
<img width="1916" height="866" alt="image" src="https://github.com/user-attachments/assets/e3ec64d4-fdae-41c8-83ff-a8ef1918a11c" />
Login Page
<img width="1891" height="871" alt="image" src="https://github.com/user-attachments/assets/0289d01a-69dc-471c-9793-b0d1a11bbcaf" />
ForgotPassword page
<img width="1918" height="862" alt="image" src="https://github.com/user-attachments/assets/ebafdf7b-a39f-4ecc-850f-ebec6004c38b" />
<img width="1918" height="867" alt="image" src="https://github.com/user-attachments/assets/212feb3a-f188-4180-8784-fb27ea614688" />
Register Page
<img width="1918" height="866" alt="image" src="https://github.com/user-attachments/assets/efab03d4-8551-4c66-9539-d0983ed0d30c" />
UserHome Page
<img width="1918" height="862" alt="image" src="https://github.com/user-attachments/assets/de582d89-e20b-4534-8de6-898281edba28" />
Upload Page
<img width="1896" height="865" alt="image" src="https://github.com/user-attachments/assets/868fe756-5f2a-4424-9d0a-f39c61a324b0" />
View Page
<img width="1917" height="855" alt="image" src="https://github.com/user-attachments/assets/0bf52fad-75af-4293-b796-08012020e269" />
Change password Page
<img width="1918" height="855" alt="Screenshot 2025-10-13 114955" src="https://github.com/user-attachments/assets/d4973519-70e6-4f47-b272-b56847fe56f7" />














💡 Future Enhancements

Multi-user support with role-based access

Cloud integration (AWS S3 / Azure Blob)

File sharing via generated links

Expiry-based document deletion

🧑‍💻 Author

Mohit Kumar Das
📧 mohitkumarpath@gmail.com

💼 GitHub - Mohit040206
