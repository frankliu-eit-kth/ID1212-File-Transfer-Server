About:
	@Author: Liming(Frank) Liu
	@contact: limingl@kth.se
	@purpose of the project:
			This file transfer server project is the 3rd homework of ID1212 Network Programming course.
		In this homework I developed a distributed file transfer client-server application which used
		RMI for inter-process communication. The server uses Java persistence APIs to maintain a database 
		which stores file information. The actual file content is transmitted through TCP blocking sockets
		and stored on a dedicated directory on server machine.
	@Code source & Copyright:
		I learned part of the structure and code from code example provided by the course: https://github.com/KTH-ID1212
		Here is the license of the code example:
			/*
			 * The MIT License
			 *
			 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
			 *
			 * Permission is hereby granted, free of charge, to any person obtaining a copy
			 * of this software and associated documentation files (the "Software"), to deal
			 * in the Software without restriction, including without limitation the rights
			 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
			 * copies of the Software, and to permit persons to whom the Software is
			 * furnished to do so, subject to the following conditions:
			 *
			 * The above copyright notice and this permission notice shall be included in
			 * all copies or substantial portions of the Software.
			 *
			 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
			 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
			 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
			 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
			 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
			 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
			 * THE SOFTWARE.
			 */
	@structure:
		there are three packages in this project:
		1.client: 
			implements MVC structure:
			-model: in client.net package: handles only the sending and receiving file tasks
			-controller: in client.net.controller package: provides network controller for view layer
			-view: in client.view package  handles the client logic which reads the command and invokes remote methods on server side then print ralted resutl on console
			
			
		2. common:
			commonly used utilities,interfaces,Exceptions
		
		3. server:
			implements MVC structure to server the client:
			-model: in Server.model package, mainly defines JPA entities
			-dao: in Server.dao package: uses JPA persistence framework to access data
			-controller: there is no controller in this project 
			-view: in server.net package, RemoteController class is a remote object, will be invoked by client remotely to execute certain logic
					other 2 classes are compoments of classic server like in the former projects. Their only tasks are sending and receiving file content.
		
			
	@What I learned:
		What I learned and achieved:
		In this homework I learned about the concept of RMI and learned to implement related Java APIs for control communication
		I learned to use JPA unit for database access and maintenance
		I learned to improve the server’s performance by using different communication paradigms. I used RMI framework for remote control and used TCP sockets for file transfer.
		
				 