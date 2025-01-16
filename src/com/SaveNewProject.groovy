package com

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.Scanner

class ProjectManager {
    def scanner = new Scanner(System.in)
    def dbUrl = "jdbc:mysql://localhost:5555/bank"
    def dbUser = "root"
    def dbPassword = "riddhmodi"
    def csvFilePath = "C:/Users/Riddh Modi/Downloads/csvFile.csv"

    def getConnection() {
        Class.forName('com.mysql.cj.jdbc.Driver')
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword)
    }

    def run() {
        while (true) {
            println "\n----------- Project Management System ------------"
            println "1. Add New Project"
            println "2. View All Projects"
            println "3. Update Project"
            println "4. Delete Project"
            println "5. Exit"
            println "Enter your choice: "

            def choice = scanner.nextInt()
            scanner.nextLine() // Consume newline

            switch (choice) {
                case 1:
                    addProject()
                    break
                case 2:
                    viewProjects()
                    break
                case 3:
                    updateProject()
                    break
                case 4:
                    deleteProject()
                    break
                case 5:
                    println "Exiting..."
                    System.exit(0)
                    break
                default:
                    println "Invalid choice!"
            }
        }
    }

    def addProject() {
        def project = inputProjectDetails()
        if (!project) {
            println "Invalid project details. Cannot create project."
            return
        }
        saveNewProjectToDatabase(project)
        saveNewProjectToCSV(project)
    }

    def inputProjectDetails() {
        println "Enter project name: "
        def projectName = scanner.nextLine()

        println "Enter project description: "
        def projectDescription = scanner.nextLine()

        println "Enter start date (yyyy-MM-dd): "
        def startDate = Date.parse('yyyy-MM-dd', scanner.nextLine())

        println "Enter end date (yyyy-MM-dd): "
        def endDate = Date.parse('yyyy-MM-dd', scanner.nextLine())

        println "Enter creator email: "
        def email = scanner.nextLine()

        def empId = fetchEmployee(email)
        if (!empId) {
            println "Employee not found with email: $email"
            return null
        }

        return [
            name: projectName,
            description: projectDescription,
            startDate: new java.sql.Date(startDate.time),
            endDate: new java.sql.Date(endDate.time),
            empId: empId
        ]
    }

    def fetchEmployee(email) {
        def connection = getConnection()
        try {
            def sql = "SELECT emp_id FROM employee WHERE email = ?"
            def preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, email)
            def resultSet = preparedStatement.executeQuery()
            return resultSet.next() ? resultSet.getInt("emp_id") : null
        } finally {
            connection?.close()
        }
    }

    def saveNewProjectToDatabase(project) {
        def connection = getConnection()
        try {
            def sql = """INSERT INTO projects(name, description, startDate, endDate, emp_id)
                         VALUES (?, ?, ?, ?, ?)"""
            def preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, project.name)
            preparedStatement.setString(2, project.description)
            preparedStatement.setDate(3, project.startDate)
            preparedStatement.setDate(4, project.endDate)
            preparedStatement.setInt(5, project.empId)

            if (preparedStatement.executeUpdate() > 0) {
                println "Project details successfully added!"
            }
        } finally {
            connection?.close()
        }
    }

    def saveNewProjectToCSV(project) {
        def csvFile = new File(csvFilePath)
        def isNewFile = !csvFile.exists()

        csvFile.withWriterAppend { writer ->
            if (isNewFile) {
                writer.writeLine("Project Name,Description,Start Date,End Date,Creator ID")
            }
            writer.writeLine("${project.name},${project.description},${project.startDate},${project.endDate},${project.empId}")
        }
        println "Project details saved in the CSV file."
    }

    def viewProjects() {
        def connection = getConnection()
        try {
            def statement = connection.createStatement()
            def resultSet = statement.executeQuery("SELECT * FROM projects")
            while (resultSet.next()) {
                println "Project ID: ${resultSet.getInt('projectId')}"
                println "Project Name: ${resultSet.getString('name')}"
                println "Description: ${resultSet.getString('description')}"
                println "---------------------------------------------------"
            }
        } finally {
            connection?.close()
        }

        println "\nProjects in CSV:"
        new File(csvFilePath).eachLine { line ->
            println line
        }
    }

    def updateProject() {
        println "Enter the project ID to update: "
        def id = scanner.nextInt()
        scanner.nextLine() // Consume newline

        def project = inputProjectDetails()
        if (!project) {
            println "Invalid project details. Cannot update."
            return
        }

        def connection = getConnection()
        try {
            def sql = """UPDATE projects
                         SET name = ?, description = ?, startDate = ?, endDate = ?, emp_id = ?
                         WHERE projectId = ?"""
            def preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, project.name)
            preparedStatement.setString(2, project.description)
            preparedStatement.setDate(3, project.startDate)
            preparedStatement.setDate(4, project.endDate)
            preparedStatement.setInt(5, project.empId)
            preparedStatement.setInt(6, projectId)

            if (preparedStatement.executeUpdate() > 0) {
                println "Project successfully updated in the database!"
            }
        } finally {
            connection?.close()
        }
    }

    def deleteProject() {
        println "Enter the project ID to delete: "
        def id = scanner.nextInt()

        def connection = getConnection()
        try {
            def sql = "DELETE FROM projects WHERE projectId = ?"
            def preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, id)

            if (preparedStatement.executeUpdate() > 0) {
                println "Project successfully deleted from the database!"
            }
        } finally {
            connection?.close()
        }
    }
}

def projectManager = new ProjectManager()
projectManager.run()
