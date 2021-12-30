# ZIO-HTTP ZIO-QUILL Example 
###
Implemented a very basic zio-http / zio-quill example

#### Libraries used / Dependencies
* zio version "1.0.13"
* zio-quill versin "3.8.0"
* h2 version "1.4.199"
* zio-http version "1.0.0.0-RC19" 

#### Artefacts:
* [ZHttpQuillMain](https://github.com/sumawa/zhttp-quill/blob/master/src/main/scala/zhq/ZHttpQuillMain.scala)
  - Shows how to set up a server and zio-http endpoints
  - Just run via IDE or sbt console
* [PersonDb](https://github.com/sumawa/zhttp-quill/blob/master/src/main/scala/zhq/PersonDb.scala)
  - Shows how to set up a ZIO Server layer 
  - It can be dependency injected and used like shown in the Main.

Describe end points for 
* adding dynamic user to H2DB. http://localhost:8090/person
* getting user(s) from H2DB.   http://localhost:8090/user/generateduuid

#### Example curl commands for testing
Inject dynamic UUID data by firing this endpoint
```
curl  http://localhost:8090/person
Persons: List(Person(102,f7743392-68d2-4acc-b941-c870c0571134,27))%                                                                       

curl  http://localhost:8090/person
Persons: List(Person(102,f7743392-68d2-4acc-b941-c870c0571134,27), Person(102,fb594b9a-4e34-4a04-b013-2d9ad1fa47cf,27))
```
Get specific user by name
```
curl http://localhost:8090/user/f7743392-68d2-4acc-b941-c870c0571134

Hello: List(Person(102,f7743392-68d2-4acc-b941-c870c0571134,27))
```

## Useful References 
* [Regarding ZLayers and Dependency Injection](https://blog.rockthejvm.com/structuring-services-with-zio-zlayer/)

#### Possible Improvements (Overall TODOs):


        

