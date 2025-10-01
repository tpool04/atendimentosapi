package br.com.tonypool.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.tonypool.security.JwtSecurity;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JwtConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable()
				.authorizeRequests()				
				.antMatchers(HttpMethod.POST, "/api/criar-conta").permitAll()
				.antMatchers(HttpMethod.POST, "/api/acessar-conta").permitAll()
				.antMatchers(HttpMethod.GET, "/api/servicos").permitAll()
				.antMatchers(HttpMethod.GET, "/api/profissionais").permitAll()
				.antMatchers(HttpMethod.POST, "/api/2fa/ativar*").permitAll()
				.antMatchers(HttpMethod.POST, "/api/2fa/ativar/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/2fa/ativar/{idCliente}").permitAll()
				.antMatchers(HttpMethod.POST, "/api/confirmar-2fa").permitAll()
				.antMatchers("/api/confirmar-2fa").permitAll()
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
				.anyRequest()
				.authenticated();
		
		http.addFilterBefore(new JwtSecurity(), UsernamePasswordAuthenticationFilter.class);
	}

	// configuração para liberar a documentação do SWAGGER
	private static final String[] SWAGGER = {
			"/v2/api-docs", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
			"/configuration/security", "/swagger-ui.html", "/webjars/**",
			"/v3/api-docs/**", "/swagger-ui/**"
	};

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(SWAGGER);
	}
}