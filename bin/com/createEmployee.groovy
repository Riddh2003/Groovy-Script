package com

import java.awt.Choice
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

import com.mysql.cj.x.protobuf.MysqlxCrud.Update

// Database Conntection step
def dbUrl = "jdbc:mysql://localhost:5555/bank"
def dbUser = "root"
def dbPassword = "riddhmodi"

def scanner= new Scanner(System.in)
def isRunnig = true;

while(isRunnig) {
    println "\n-------------Employee Management System-------------"
    println "1. Add Employee"
    println "2. View All Employee"
    println "3. Update Employee"
    println "4. Delete Employee"
    println "5. Exit"
    println "Choose an option : "
    def choice= scanner.nextLine()

    switch(choice) {
        case "1":
            addEmployee(dbUrl,dbUser,dbPassword)
            break
        case "2":
            viewAllEmployees(dbUrl,dbUser,dbPassword)
            break
        case "3":
            updateEmployee(dbUrl,dbUser,dbPassword)
            break
        case "4":
            deleteEmployee(dbUrl,dbUser,dbPassword)
            break
        case "5":
            isRunnig = false;
            println "Exit for the Employee Management System. GoodBye!"
            break
        default:
            println "Invaild Choice. Please try again."
    }
}

// add employee method call
def addEmployee(dbUrl,dbUser,dbPassword) {
    def scanner= new Scanner(System.in)
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

    def sql = """Insert into employee(name,email,password,phoneNo,gender,address) values(?,?,?,?,?,?)"""

    exceuteUpdate(sql,dbUrl,dbUser,dbPassword,[employeeName,employeeEmail,employeePassword,employeePhoneNo,employeeGender,employeeAddress])
    println "Employee successfully updated."
}

// view all the employees
def viewAllEmployees(dbUrl,dbUser,dbPassword) {
    def sql = """select * from employee"""

    executeQuery(sql,dbUrl,dbUser,dbPassword){ resultSet ->
        println "\nId | Name | Email | Password | PhoneNo | Gender | Address"
        println "--------------------------------------------------------------------------------------------------------------------"
        while (resultSet.next()) {
            println "${resultSet.getInt('emp_id')} | ${resultSet.getString('name')} | ${resultSet.getString('email')} | ${resultSet.getString('password')} | ${resultSet.getString('phoneNo')} | ${resultSet.getString('gender')} | ${resultSet.getString('address')}"
        }
    }
}

// update the employee details
def  updateEmployee(dbUrl,dbUser,dbPassword) {
    def scanner= new Scanner(System.in)
    println "Enter the employee id : "
    def id = scanner.nextLine();

    println "Enter new details(leave blank to keep unchanged)"
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

    def sql = """update employee set name= COALESCE(NULLIF(?,''),employeeName),
                                    email= COALESCE(NULLIF(?,''),employeeEmail),
                                    password= COALESCE(NULLIF(?,''),employeePassword),
                                    phoneNo= COALESCE(NULLIF(?,''),employeePhoneNo),
                                    gender= COALESCE(NULLIF(?,''),employeeGender),
                                    address= COALESCE(NULLIF(?,''),employeeAddress)
                                    where emp_id = ?"""
    exceuteUpdate(sql,dbUrl,dbUser,dbPassword,[id])
    println("Employee updated sucessfully.")
}

//delete employee
def deleteEmployee(dbUrl,dbUser,dbPassword) {
    def scanner= new Scanner(System.in)
    println "Enter the employee id to delete :"
    def id = scanner.nextLine();

    def sql = """delete from employee where emp_id = ?"""
    exceuteUpdate(sql,dbUrl,dbUser,dbPassword,[id])
}

// utility method to execute query
def executeQuery(sql,dbUrl,dbUser,dbPassword,resultProcesser) {
    Connection connection = null
    try {
        Class.forName("com.mysql.cj.jdbc.Driver")
        connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword)
        def statment = connection.createStatement()
        def resultSet= statment.executeQuery(sql)
        resultProcesser(resultSet)
    }catch(Exception e) {
        println "Error : ${e.message}"
    }
}

// utility method to execute an update
def exceuteUpdate(sql,dbUrl,dbUser,dbPassword,params = []) {
    Connection connection = null
    try {
        Class.forName("com.mysql.cj.jdbc.Driver")
        connection = DriverManager.getConnection(dbUrl,dbUser,dbPassword)
        def preparedStatement = connection.prepareStatement(sql)
        params.eachWithIndex { param,index ->
            preparedStatement.setObject(index++, param)
        }
        preparedStatement.executeUpdate()
    }catch(Exception e) {
        println "Error : ${e.message}"
    }
}
