# Camel Ruby N2N Component

A custom Apache Camel component that enables running Ruby scripts within Camel routes. This component allows you to leverage Ruby's powerful scripting capabilities while maintaining access to Camel's exchange variables and providing multiple ways to define scripts.

## Features

- Execute Ruby scripts within Camel routes
- Support for inline, Base64-encoded, and file-based scripts
- Full access to Camel exchange variables
- Built-in helper methods for common operations
- Script caching for improved performance
- Thread-safe execution

## Installation

Add the following dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.n2nlab</groupId>
    <artifactId>camel-ruby-n2n</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Component Options

The Ruby component has no specific options at the component level.

## Endpoint Options

| Name | Type | Default | Description |
|------|------|---------|-------------|
| scriptName | String | | **Required** Name identifier for the script |
| rubyScript | String | | Inline Ruby script content |
| encodedScript | String | | Base64 encoded Ruby script content |
| scriptPath | String | | Path to external Ruby script file |
| cacheScript | boolean | false | Whether to cache the compiled script |
| allowNullBody | boolean | true | Whether to convert null results to empty strings |
| encoding | String | UTF-8 | Character encoding when reading script files |

Note: You must specify exactly one of `rubyScript`, `encodedScript`, or `scriptPath`.

## Available Variables

The following variables are available in your Ruby scripts:

| Variable | Type | Description |
|----------|------|-------------|
| $exchange | Exchange | The Camel Exchange object |
| $message | Message | The current Message |
| $body | Object | The message body |
| $headers | Map | The message headers |
| $properties | Map | The exchange properties |

## Helper Methods

The component provides several helper methods to make script writing easier:

| Method | Description |
|--------|-------------|
| set_body(val) | Set the message body |
| set_header(name, val) | Set a message header |
| get_header(name) | Get a message header value |
| set_exchange_property(name, val) | Set an exchange property |
| get_exchange_property(name) | Get an exchange property value |

## Usage Examples

### Simple Inline Script

```java
from("direct:start")
    .to("ruby:uppercase?rubyScript=$body.to_s.upcase");
```

### Multi-line Script (Base64 Encoded)

```java
String script = """
    input = $body.to_s.strip
    set_header('ProcessedBy', 'RubyScript')
    transformed = input.upcase
    transformed
    """;
String encodedScript = RubyScriptUtils.encodeScript(script);
from("direct:start")
    .to("ruby:transform?encodedScript=" + encodedScript);
```

### Script from File

```java
from("direct:start")
    .to("ruby:transform?scriptPath=/path/to/script.rb");
```

### Header Manipulation

```java
from("direct:start")
    .to("ruby:headers?rubyScript=set_header('OutputHeader', get_header('InputHeader'))");
```

### Property Manipulation

```java
from("direct:start")
    .to("ruby:properties?rubyScript=val = get_exchange_property('InputProperty'); set_exchange_property('OutputProperty', val + '-modified')");
```

## Script File Example

Here's an example of a Ruby script file that demonstrates various features:

```ruby
# Get the input body and normalize whitespace
input = $body.to_s.gsub(/\s+/, ' ').strip

# Add processing metadata
set_header('ProcessedBy', 'RubyScript')
set_header('ProcessingTime', Time.now.to_s)

# Transform the data
transformed = input.upcase

# Add metadata
set_exchange_property('originalLength', input.length)
set_exchange_property('transformedLength', transformed.length)

# Set status based on length
set_header('Status', transformed.length > 100 ? 'LARGE_MESSAGE' : 'NORMAL')

# Return transformed data
transformed
```

## Thread Safety

The component is designed to be thread-safe:
- Uses `LocalContextScope.THREADSAFE` for JRuby container
- Properly manages script container lifecycle
- Cleans up resources after each execution

## Error Handling

The component provides detailed error messages and proper exception handling:
- Script syntax errors are caught and reported
- Runtime errors include full stack traces
- Resource cleanup is guaranteed through try-finally blocks

## Build

To build the component:

```bash
mvn clean install
```

## License

Apache License 2.0

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.