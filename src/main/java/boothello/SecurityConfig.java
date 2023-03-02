package boothello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@Order(value = 2)
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    void init() {
        setDataSource(DataSourceBuilder.create().build());
    }

    protected void configure(HttpSecurity http) throws Exception
    {
        http.csrf().disable().authorizeHttpRequests().anyRequest().authenticated().and().httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception
    {
        auth.jdbcAuthentication().passwordEncoder( SecurityConfig.passwordEncoder())
                .dataSource(getDataSource())
                .usersByUsernameQuery("select username,password,enabled "
                        + "from users "
                        + "where username = ? ")
                .authoritiesByUsernameQuery("select username,role "
                        + "from roles "
                        + "where username = ?");
        /*auth.inMemoryAuthentication()
                .withUser("admin")
                .password("{noop}password")
                .roles("USER");*/

    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
