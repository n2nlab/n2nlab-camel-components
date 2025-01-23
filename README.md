# N2NLab Camel Components

This repository contains a collection of Apache Camel components developed by N2NLab. These components extend Apache Camel's functionality with additional integration capabilities.

## Available Components

| Component     | Description | Documentation |
|---------------|-------------|---------------|
| ruby | Execute Ruby scripts within Camel routes | [README](./camel-ruby-n2n/README.md) |
| pythonN2N     | Execute Python scripts within Camel routes | [README](./camel-python-n2n/README.md) |

## Requirements

- Java 17 or higher
- Apache Maven 3.8.0 or higher
- Apache Camel 4.8.0 or higher

## Installation

Add the following dependency to your project's `pom.xml`:

```xml
<dependency>
<!--TO BE ADDED-->
</dependency>
```

Replace `[component-name]` with the desired component artifact ID (e.g., `camel-ruby-n2n`).

## Building from Source

1. Clone the repository:
```bash
git clone https://github.com/n2nlab/n2nlab-camel-components.git
```

2. Build all components:
```bash
cd n2nlab-camel-components
mvn clean install
```

3. Build specific component:
```bash
mvn clean install -pl camel-ruby-n2n
```

## Component Status

| Component | Status | Latest Version |
|-----------|--------|----------------|
| camel-ruby-n2n | In Development | -              |
| camel-python-n2n | In Development | -              |


## Features Common to All Components

- Thread-safe execution
- Support for inline and file-based scripts
- Base64 encoded script support for complex multi-line scripts
- Full access to Camel Exchange, Message, and Headers
- Comprehensive error handling
- Detailed logging
- Extensive test coverage

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Development Guidelines

1. Follow Apache Camel component development best practices
2. Maintain consistent code style
3. Include comprehensive unit tests
4. Update component documentation
5. Test with different Camel versions

## License

Apache License 2.0

## Support

- Create an issue in the GitHub repository
- Contact the maintainers at [mahmoudahmedxyz@gmail.com]
- Check individual component documentation for specific issues

## Version Compatibility

| Component | Camel Version | Java Version |
|-----------|--------------|--------------|
| ruby      | 4.8.0+ | 17+ |
| pythonN2N | 4.8.0+ | 17+ |


## Roadmap

### Planned Components

- discord-n2n: integration with discord
- camel-node-n2n: Node.js script execution
- camel-r-n2n: R script execution
- camel-julia-n2n: Julia script execution

### Future Improvements
- Enhanced script caching
- Performance optimizations
- Additional language support
- Cloud-native features
- Container-friendly configurations

## Related Projects

- [Apache Camel](https://camel.apache.org/)
- [N2NLab Projects](https://github.com/n2nlab)

## Acknowledgments

- Apache Camel Community
- JRuby Team
- Open Source Contributors

## Code of Conduct

This project follows the Apache Code of Conduct. Please read [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for details.