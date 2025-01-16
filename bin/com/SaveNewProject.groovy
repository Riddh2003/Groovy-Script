package com

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

import org.codehaus.groovy.classgen.ReturnAdder

// Database Conntection step
def dbUrl = "jdbc:mysql://localhost:5555/bank"
def dbUser = "root"
def dbPassword = "riddhmodi"

//csv file path
def csvFilePath = "C:/Users/Riddh Modi/Downloads/csvFile.csv";

//Input Parameter from the console
def scanner = new Scanner(System.in)
print "Enter project creater name: "
def createrName = scanner.nextLine()
print "Enter project creater email: "
def createrEmail = scanner.nextLine()
print "Enter Project Name: "
def projectName = scanner.nextLine()
print "Enter Project Description: "
def projectDescription = scanner.nextLine()
print "Enter Start Date(DD-MM-YYYY): "
def startDate = scanner.nextLine()
print "Enter End Date(DD-MM-YYYY): "
def endDate = scanner.nextLine()

// Validation
if(!projectName || projectName.trim().isEmpty()) {
    throw new IllegalArgumentException("Project Name is requried.")
}

if(startDate && endDate && startDate>endDate) {
    throw new IllegalArgumentException("Start Date cannot be after End Date.")
}

def empId = fetchEmployee(createrEmail,dbUrl,dbUser,dbPassword)
if(!empId) {
    println "No employee found with email: ${createrEmail}"
    return
}

// use of map
def newProject = [
    name : projectName,
    description : projectDescription,
    startDate: startDate,
    endDate: endDate,
    emp_id: empId
    ]

// Save the new project method calling
saveNewProjectToDatabase(newProject,dbUrl,dbUser,dbPassword)

// all details add into csv file write into form the database
saveNewProjectToCSV(newProject,csvFilePath)

//output success message
println "New Project '${projectName}' added successfully."

// fetch employee form the database
def fetchEmployee(email,dbUrl,dbUser,dbPassword) {
    Connection connection = null;
    try {
        Class.forName('com.mysql.cj.jdbc.Driver')

        connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword)

        def sql = "Select emp_id from employee where email = ?"
        def preparedStatement= connection.prepareStatement(sql)
        preparedStatement.setString(1, email)

        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            return resultSet.getInt('emp_id');
        }
        else {
            return null;
        }

    } catch (e) {
        e.printStackTrace()
    }
}

// saving new project method
def saveNewProjectToDatabase(project,dbUrl,dbUser,dbPassword) {
    // here we can call api or database insert query for store the data.
    Connection connection = null;
    try {
        Class.forName('com.mysql.cj.jdbc.Driver')
        connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword)

        def sql = """Insert into projects(name,description,startDate,endDate,emp_id) values(?,?,?,?,?)"""

        def preparedStatement = connection.prepareStatement(sql)
        preparedStatement.setString(1, project.name)
        preparedStatement.setString(2, project.description)
        preparedStatement.setString(3, project.startDate)
        preparedStatement.setString(4, project.endDate)
        preparedStatement.setInt(5, project.emp_id)

        int rows = preparedStatement.executeUpdate()
        if(rows>0) {
            println "Project details successfully added!"
        }
    }catch(Exception e) {
        println "Error: ${e.message}"
    }
}

def saveNewProjectToCSV(project,csvFilePath) {
    def csvFile = new File(csvFilePath);
    def isNewFile = !csvFile.exists();
    //open the file in append mode
    csvFile.withWriterAppend{writer->
        // check for csv file exists or not
        if(isNewFile) {
            writer.writeLine("Project Name,Description,Start Date,End Date,CreaterId")
        }
        //write the project data as a new row
        writer.writeLine("${project.name},${project.description},${project.startDate},${project.endDate},${project.emp_id}")
    }
    println "Project details save in the csv file."
}
