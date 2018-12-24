# osgi-platform-base
OSGi bundle to form the base of a stepwise application
The primary goal of this bundle is to provide OSGi extension points and services to simplify generic application operations. 
The public APIs should only be as complex as our application demands them to be.  The following features will be examined:
- Pluggable authentication and ABAC authorization
- JAX-RS resource deployment/metrics/and management
- Structured logging with pluggable appenders (Splunk integration)
- Server side extensions to client side UI constructs via ReactJs Components
