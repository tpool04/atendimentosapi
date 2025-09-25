package br.com.tonypool.security;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenSecurity {

	/*
	 * Método utilizado para gerar o TOKEN
	 */
	public static String generateToken(String cpf, Integer idCliente) {

		// chave secreta para geração do TOKEN (Evitar falsificações)
		String secretKey = JwtSecurity.SECRET;

		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");

		String token = Jwts.builder().setId("atendimentosapi").setSubject(cpf)
				.claim("idCliente", idCliente)
				.claim("authorities",
						grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 6000000))
				.signWith(SignatureAlgorithm.HS512, secretKey.getBytes()).compact();

		return token;
	}

	/*
	 * Método para ler o usuário do usuário gravado no TOKEN
	 */
	public static String getUserFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	private static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {

		String secretKey = JwtSecurity.SECRET;
		final Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();

		return claimsResolver.apply(claims);
	}

	public static Integer getIdClienteFromToken(String token) {
		String secretKey = JwtSecurity.SECRET;
		final Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
		Object idObj = claims.get("idCliente");
		if (idObj == null) return null;
		if (idObj instanceof Integer) return (Integer) idObj;
		if (idObj instanceof Number) return ((Number) idObj).intValue();
		if (idObj instanceof String) return Integer.valueOf((String) idObj);
		throw new IllegalArgumentException("idCliente claim is not a valid Integer");
	}

}