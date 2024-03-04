<div id="top"></div>

<h1 align="center">Openstack Swift Stateless Client</h1>

<p align="center">
<a href="https://github.com/Trendyol/kafkathena-commons/blob/next/LICENSE">
    <img src="https://img.shields.io/github/v/release/Trendyol/kafkathena-commons" alt="Release" />
  </a>
<a href="https://img.shields.io/badge/spring%20boot-2.x%7C3.x-orange">
    <img src="https://img.shields.io/badge/spring%20boot-3.x-orange" alt="License" />
  </a>
  <a href="https://github.com/Trendyol/kafkathena-commons/blob/next/LICENSE">
    <img src="https://img.shields.io/github/license/trendyol/baklava" alt="Spring Boot Version" />
  </a>
</p>

<!-- ABOUT THE PROJECT -->
## About The Project
Openstack Stateless Client implementation. This library uses openstack4j to connect to openstack swift cluster.

<!-- Features -->
## Features

* Spring Auto Configuration
* Application Credential and Username Password Authentication Support
* In memory token refresh support.
* In memory set temp url key by account support
* Utility functions support. (Get, Put, Create Container, Get Download Url, Get Upload Url)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- Build With -->
### Built With

This section should list any major frameworks/libraries used to bootstrap your project. Leave any add-ons/plugins for the acknowledgements section. Here are a few examples.

* [Openstack4j]
* [Spring Starter 3+]
* [Jdk 17]

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

### Prerequisites

This is an example of how to list things you need to use the software and how to install them.
* Maven 3+
* Jdk 17

### Installation
1. Copy and paste this inside your pom.xml dependencies block.
```xml
<dependency>
  <groupId>com.trendyol</groupId>
  <artifactId>openstack-client</artifactId>
  <version>RELEASE</version>
</dependency>
```

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## Usage

1. Add dependency to pom
2. Add Configuration yaml to project
2. Inject OpenstackManager
3. Use the functions

```

openstack:
  swift:
    auth-host: <authUrl>
    object-storage-host: <objectStorageHost>
    storage-public-host: <storagePublicHost>
    default-region: <defaultRegion>
    project-id: <projectId>
    username-password-credential:
      id: <userId>
      password: <password>
    application-credential:
      credential-id: <credentialId>
      secret: <secret>
    temp-url-keys:
      - region: <region>
        key-1: <key-1>
        key-2: <key-2>
    meta:
      set-temp-key-cycle-minutes: <tempUrlKeysSetTimeInMinutes> #default 6 hours
    web-client:
      connect-timeout-millis: 10000
      read-timeout-millis: 10000
```

```
public class OpenstackService {
    private final OpenstackManager manager;

    public List<TempUrl> getDownloadUrl() {
        String filePath = "filepath";
        String containerName = "container";
        TempUrl downloadTempUrl = manager.getDownloadTempUrl("region", containerName, filePath, 30);
    }
    
    public SwiftObject getObject() {
        String filePath = "filepath";
        String containerName = "container";
        return manager.getObject("region", containerName, filePath, 30);
    }
    
    public boolean isExist() {
        String filePath = "filepath";
        String containerName = "container";
        return manager.existObject("region", containerName, filePath, 30);
    }
    
    public putObject() {
        String filePath = "filepath";
        byte[] fileBytes = new byte[10]
        String containerName = "container";
        manager.putObject("earth", containerName, filePath, fileBytes);
    }
    
```

```

License
--------

    MIT License

    Copyright (c) 2022 Trendyol
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 ```

<p align="right">(<a href="#top">back to top</a>)</p>
