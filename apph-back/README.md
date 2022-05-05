# Apph Backend

Using springboot and maven.

## AWS S3 Setting

In your application-local.properties, add the following settings:

```properties
accessKey=YourAccessKey
secretKey=YourSecretKey
bucketName=BucketName
region=region
user=${your_name}/
```

## Run project

In the apph-back directory, you can run to test the api:

### `mvn spring-boot:run`

## Code quality

We use Sonarlint to find and fix bugs, vulnerabilities and code smells. To install the InteliJ plugin go to : 
file > Settings > Plugins > Marketplace and shearch for 'Sonarlint'
After restarting you IDE, sonarlint show warnings and you can analyse a file with Ctrl+Maj+s

## WebSecurityConfig Configuration 

Give access to API given in the function `antMachers()`.

Example:

```java
protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .authorizeRequests()
                .antMatchers("/api/test/").permitAll()
                .antMatchers("/api/admin/**").hasAuthority("ADMIN")
                .antMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated();
    }
```

##DataBase Configuration
In your application-local.properties, add the setting:
```properties
init-database=true
```
In order to initialize the database.