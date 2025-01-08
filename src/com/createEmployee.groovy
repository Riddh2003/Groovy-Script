package com

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

// Database Conntection step
def dbUrl = "jdbc:mysql://localhost:5555/bank"
def dbUser = "root"
def dbPassword = "riddhmodi"

def scanner= new Scanner(System.in);

print "Enter employee name : "
def employeeName = scanner.nextLine();
print "Enter employee email : "
def employeeEmail = scanner.nextLine();
print "Enter employee Password : "
def employeePassword = scanner.nextLine();
print "Enter employee phoneNumber : "
def employeePhoneNo = scanner.nextLine();
print "Enter employee Gender : "
def employeeGender = scanner.nextLine();
print "Enter employee Address : "
def employeeAddress = scanner.nextLine();

def employeeDetails = [
    name     : employeeName,
    email    : employeeEmail,
    password : employeePassword,
    phoneNo  : employeePhoneNo,
    gender   : employeeGender,
    address  : employeeAddress
]

def saveToDatabase(employeeDetails,dbUrl,dbUser,dbPassword) {
	Connection connection = null; 
	try {
		Class.forName('com.mysql.cj.jdbc.Driver')
		connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword)
		println("Database connection successful!")
		def sql = """Insert into employee(name,email,password,phoneNo,gender,address) values(?,?,?,?,?,?)"""
		
		def preparedStatement = connection.prepareStatement(sql)
		preparedStatement.setString(1, employeeDetails.name)
		preparedStatement.setString(2, employeeDetails.email)
		preparedStatement.setString(3, employeeDetails.password)
		preparedStatement.setString(4, employeeDetails.phoneNo)
		preparedStatement.setString(5, employeeDetails.gender)
		preparedStatement.setString(6, employeeDetails.address)
		
		int rows = preparedStatement.executeUpdate()
		if(rows>0) {
			println "Employee details successfully added!"
		}
	}catch(Exception e) {
		println "Error: ${e.message}"
	}finally {
		if(connection != null) {
			connection.close();
			println "Database connection closed."
		}
	}
}

saveToDatabase(employeeDetails, dbUrl, dbUser, dbPassword)