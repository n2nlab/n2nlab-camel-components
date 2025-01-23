# N2NLab Camel PythonN2N Component

An Apache Camel component by [N2NLab](https://n2nlab.com) for executing Python3 scripts with advanced features. This component allows seamless integration between Java/Camel routes and Python3 scripts.

## Features

- Execute Python3 scripts from Camel routes
- Support for Python 2.x and 3.x
- Script templates
- Module preloading
- Timeout handling
- Debug mode
- Cross-platform compatibility
- Configurable Python environment

## Requirements

- Java 17 or higher
- Apache Camel **4.8.0** or higher
- Python installed on the system (2.x or 3.x)

## Installation

Add this dependency to your project's pom.xml:

**Step 1.** Add the JitPack repository to your build file

```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
**Step 2**. Add the dependency

```xml	
    <dependency>
	    <groupId>com.github.n2nlab</groupId>
	    <artifactId>camel-pythonN2N</artifactId>
	    <version>-SNAPSHOT</version>
	</dependency>
```

## Usage

Basic example:

```java
from("direct:start")
    .to("pythonN2N:processor?pythonScript=result = body * 2")
    .log("Result: ${body}");
```

With advanced features:

```java
from("direct:start")
    .to("pythonN2N:processor?" +
        "scriptTemplate=common_functions.py&" +
        "pythonScript=result = process_data(body)&" +
        "timeout=5000&" +
        "debug=true")
    .log("Result: ${body}");
```

## Configuration Options

| Parameter | Type | Required | Default Value | Description |
|-----------|------|----------|---------------|-------------|
| name | String | true |  | Name of the Camel route |  
| pythonScript | String | false |  | The Python script to execute |
| scriptTemplate | String | false |  | Template script to include before the main script |
| preloadPythonModules | boolean | false | false | Whether to preload Python modules |
| requiredModules | String | false |  | Comma-separated list of required Python modules |
| pythonPath | String | false |  | Custom Python executable path |
| debug | boolean | false | false | Enable debug mode |
| timeout | int | false | 30000 | Script execution timeout in milliseconds |  
| keepTempFiles | boolean | false | false | Keep temporary files for debugging |
| encoding | String | false | "UTF-8" | Character encoding for scripts and data |
| returnFullOutput | boolean | false | false | Return full Python execution output |

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

Copyright (c) 2025 N2NLab. All rights reserved.
Licensed under the Apache License, Version 2.0. http://www.apache.org/licenses/LICENSE-2.0