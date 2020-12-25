[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <img src="logo.png" alt="Logo" width="75" height="90">

  <h3 align="center">A chat bot creation Java library</h3>
</p>

<!-- ABOUT THE PROJECT -->
## About The Project

Written in Java 11, CBBot is a library for creating chat bots. What makes it special, and what gives it its name (ControllerBasedBot), is its ability to create bots that are portable to any web-based chat service (as long as there's a MessagingController made for it).

Currently, there are MessagingControllers written for the following services:
* WhatsApp (WEB version)
* Microsoft Teams Chat (WEB version)

Although the list is limited, it is fairly easy to create controllers for other web-based chat services.

### Built With

* [Selenium](https://www.selenium.dev/)


## Getting Started
### Prerequisites
* Compatible Java installation
* A selenium Web Driver

### Installation
[Download the .jar file here.](https://www.mediafire.com/file/ykqduat2fd0eg5s/CBBot.jar/file)


## Usage
For creating a bot, create a class that extends from the CBBot abstract class.
For creating a messaging controller, create a class that extends from the MessagingController interface.
