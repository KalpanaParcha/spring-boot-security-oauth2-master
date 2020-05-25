package util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")

	private String secret;

	//retrieve username from jwt token
	public String extractUserName(String token) {

		return extractClaim(token, Claims::getSubject);

	}
	//retrieve expiration date from jwt token
	public Date extractExpiration(String token) {

		return extractClaim(token, Claims::getExpiration);

	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

	final Claims claims = extractAllClaims(token);

	return claimsResolver.apply(claims);

	}

	    //for retrieveing any information from token we will need the secret key

	private Claims extractAllClaims(String token) {

	return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

	}

	//check if the token has expired

	private Boolean isTokenExpired(String token) {

	final Date expiration = extractExpiration(token);

	return expiration.before(new Date());

	}

	//generate token for user

	public String generateToken(UserDetails userDetails) {

	Map<String, Object> claims = new HashMap<>();

	return createToken(claims, userDetails.getUsername());

	}

	//while creating the token -

	//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID

	//2. Sign the JWT using the HS256 algorithm and secret key.

	//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)

	//   compaction of the JWT to a URL-safe string 

	private String createToken(Map<String, Object> claims, String subject) {

	return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))

	.setExpiration(new Date(System.currentTimeMillis() + (60*60*10*1000)))

	.signWith(SignatureAlgorithm.HS256, secret).compact();

	}

	//validate token

	public Boolean validateToken(String token, UserDetails userDetails) {

	final String username = extractUserName(token);

	return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

	}

}
	

