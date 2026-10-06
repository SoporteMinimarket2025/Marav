package com.ecomarket.config;

import com.ecomarket.security.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * ============================================================
 * CONFIGURACIÓN DE SEGURIDAD
 * ============================================================
 *
 * EcoMarket PRO
 *
 * Roles utilizados:
 *
 * ADMIN
 * EMPLEADO
 *
 * ADMIN:
 * - Puede consultar.
 * - Puede buscar.
 * - Puede crear.
 * - Puede editar.
 * - Puede eliminar.
 *
 * EMPLEADO:
 * - Puede consultar.
 * - Puede buscar.
 * - No puede modificar información.
 *
 * ============================================================
 */
@Configuration
public class SecurityConfig {


    // ============================================================
    // SERVICIO DE USUARIOS
    // ============================================================

    private final CustomUserDetailsService userDetailsService;


    public SecurityConfig(
            CustomUserDetailsService userDetailsService
    ) {

        this.userDetailsService = userDetailsService;

    }


    // ============================================================
    // ENCODER DE CONTRASEÑAS
    // ============================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }


    // ============================================================
    // PROVEEDOR DE AUTENTICACIÓN
    // ============================================================

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider auth =
                new DaoAuthenticationProvider();

        auth.setUserDetailsService(userDetailsService);

        auth.setPasswordEncoder(passwordEncoder());

        return auth;

    }


    // ============================================================
    // CONFIGURACIÓN PRINCIPAL DE SEGURIDAD
    // ============================================================

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {


        http

                // ====================================================
                // CSRF
                // ====================================================
                //
                // Se desactiva porque las operaciones AJAX del
                // proyecto utilizan APIs REST.
                //
                // ====================================================

                .csrf(csrf -> csrf.disable())


                // ====================================================
                // AUTORIZACIONES
                // ====================================================

                .authorizeHttpRequests(auth -> auth


                        // =================================================
                        // RUTAS PÚBLICAS
                        // =================================================

                        .requestMatchers(

                                "/",
                                "/login",
                                "/registro",
                                "/recuperar",
                                "/enviar-recuperacion",
                                "/reset-password",
                                "/guardar-password",

                                // CSS
                                "/css/**",

                                // JavaScript
                                "/js/**",

                                // Imágenes
                                "/images/**"

                        ).permitAll()


                        // =================================================
                        // USUARIOS - PÁGINA
                        // =================================================

                        .requestMatchers(

                                "/usuarios"

                        ).hasAnyRole(
                                "ADMIN",
                                "EMPLEADO"
                        )


                        // =================================================
                        // USUARIOS - CONSULTA PROPIA
                        // =================================================

                        .requestMatchers(

                                "/api/usuarios/mios"

                        ).hasAnyRole(
                                "ADMIN",
                                "EMPLEADO"
                        )


                        // =================================================
                        // USUARIOS - RESTO DE API
                        // =================================================

                        .requestMatchers(

                                "/api/usuarios/**"

                        ).hasRole("ADMIN")


                        // =================================================
                        // PROVEEDORES - PÁGINA
                        // =================================================

                        .requestMatchers(

                                "/proveedores"

                        ).hasAnyRole(
                                "ADMIN",
                                "EMPLEADO"
                        )


                        // =================================================
                        // PROVEEDORES - CONSULTA
                        // =================================================

                        .requestMatchers(

                                HttpMethod.GET,
                                "/proveedores/api",
                                "/proveedores/api/**"

                        ).hasAnyRole(
                                "ADMIN",
                                "EMPLEADO"
                        )


                        // =================================================
                        // PROVEEDORES - CREAR
                        // =================================================

                        .requestMatchers(

                                HttpMethod.POST,
                                "/proveedores/api",
                                "/proveedores/api/**"

                        ).hasRole("ADMIN")


                        // =================================================
                        // PROVEEDORES - EDITAR
                        // =================================================

                        .requestMatchers(

                                HttpMethod.PUT,
                                "/proveedores/api",
                                "/proveedores/api/**"

                        ).hasRole("ADMIN")


                        // =================================================
                        // PROVEEDORES - ELIMINAR
                        // =================================================

                        .requestMatchers(

                                HttpMethod.DELETE,
                                "/proveedores/api",
                                "/proveedores/api/**"

                        ).hasRole("ADMIN")


                        // =================================================
                        // REPORTES Y CONFIGURACIÓN
                        // =================================================
                        //
                        // Estos módulos continúan siendo exclusivos
                        // del ADMINISTRADOR.
                        //
                        // IMPORTANTE:
                        //
                        // /caja/** YA NO ESTÁ AQUÍ.
                        //
                        // =================================================

                        .requestMatchers(

                                "/reportes/**",
                                "/configuracion/**"

                        ).hasRole("ADMIN")


                        // =================================================
                        // CAJA - PÁGINA
                        // =================================================
                        //
                        // ADMIN:
                        // Puede consultar y administrar.
                        //
                        // EMPLEADO:
                        // Puede consultar.
                        //
                        // =================================================

                        .requestMatchers(

                                "/caja"

                        ).hasAnyRole(
                                "ADMIN",
                                "EMPLEADO"
                        )


                        // =================================================
                        // CAJA - CONSULTA
                        // =================================================
                        //
                        // GET:
                        // ADMIN + EMPLEADO
                        //
                        // Permite:
                        //
                        // GET /api/caja
                        // GET /api/caja/abierta
                        //
                        // =================================================

                        .requestMatchers(

                                HttpMethod.GET,
                                "/api/caja",
                                "/api/caja/**"

                        ).hasAnyRole(
                                "ADMIN",
                                "EMPLEADO"
                        )


                        // =================================================
                        // CAJA - ABRIR
                        // =================================================
                        //
                        // POST:
                        // SOLAMENTE ADMIN.
                        //
                        // =================================================

                        .requestMatchers(

                                HttpMethod.POST,
                                "/api/caja",
                                "/api/caja/**"

                        ).hasRole("ADMIN")


                        // =================================================
                        // CAJA - INGRESO
                        // =================================================
                        //
                        // PUT:
                        // SOLAMENTE ADMIN.
                        //
                        // =================================================

                        .requestMatchers(

                                HttpMethod.PUT,
                                "/api/caja/ingreso"

                        ).hasRole("ADMIN")


                        // =================================================
                        // CAJA - EGRESO
                        // =================================================
                        //
                        // PUT:
                        // SOLAMENTE ADMIN.
                        //
                        // =================================================

                        .requestMatchers(

                                HttpMethod.PUT,
                                "/api/caja/egreso"

                        ).hasRole("ADMIN")


                        // =================================================
                        // CAJA - CERRAR
                        // =================================================
                        //
                        // PUT:
                        // SOLAMENTE ADMIN.
                        //
                        // =================================================

                        .requestMatchers(

                                HttpMethod.PUT,
                                "/api/caja/cerrar"

                        ).hasRole("ADMIN")


                        // =================================================
                        // ABONOS - PÁGINA
                        // =================================================

                        .requestMatchers(

                                "/abonos"

                        ).hasAnyRole(
                                "ADMIN",
                                "EMPLEADO"
                        )


                        // =================================================
                        // ABONOS - CONSULTA
                        // =================================================

                        .requestMatchers(

                                HttpMethod.GET,
                                "/api/abonos",
                                "/api/abonos/**"

                        ).hasAnyRole(
                                "ADMIN",
                                "EMPLEADO"
                        )


                        // =================================================
                        // ABONOS - CREAR
                        // =================================================

                        .requestMatchers(

                                HttpMethod.POST,
                                "/api/abonos",
                                "/api/abonos/**"

                        ).hasRole("ADMIN")


                        // =================================================
                        // ABONOS - EDITAR
                        // =================================================

                        .requestMatchers(

                                HttpMethod.PUT,
                                "/api/abonos",
                                "/api/abonos/**"

                        ).hasRole("ADMIN")


                        // =================================================
                        // ABONOS - ELIMINAR
                        // =================================================

                        .requestMatchers(

                                HttpMethod.DELETE,
                                "/api/abonos",
                                "/api/abonos/**"

                        ).hasRole("ADMIN")


                        // =================================================
                        // MÓDULOS ADMIN + EMPLEADO
                        // =================================================

                        .requestMatchers(

                                // Dashboard
                                "/dashboard",

                                // Ventas
                                "/ventas/**",

                                // Facturas
                                "/facturas/**",

                                // Devoluciones
                                "/devoluciones/**",

                                // Clientes
                                "/clientes/**",

                                // Productos
                                "/productos/**",

                                // Inventario
                                "/inventario/**",

                                // Simulador
                                "/simulador/**",


                                // =================================================
                                // API VENTAS
                                // =================================================

                                "/api/ventas/**",


                                // =================================================
                                // API FACTURAS
                                // =================================================

                                "/api/facturas/**",


                                // =================================================
                                // API DEVOLUCIONES
                                // =================================================

                                "/api/devoluciones/**",


                                // =================================================
                                // API CLIENTES
                                // =================================================

                                "/api/clientes/**",


                                // =================================================
                                // API PRODUCTOS
                                // =================================================

                                "/api/productos/**",


                                // =================================================
                                // API INVENTARIO
                                // =================================================

                                "/api/inventario/**"

                        ).hasAnyRole(
                                "ADMIN",
                                "EMPLEADO"
                        )


                        // =================================================
                        // CUALQUIER OTRA RUTA
                        // =================================================

                        .anyRequest().authenticated()

                )


                // ====================================================
                // LOGIN
                // ====================================================

                .formLogin(form -> form

                        .loginPage("/login")

                        .loginProcessingUrl("/login")

                        .usernameParameter("email")

                        .passwordParameter("password")

                        .defaultSuccessUrl(
                                "/dashboard",
                                true
                        )

                        .failureUrl(
                                "/login?error=true"
                        )

                        .permitAll()

                )


                // ====================================================
                // LOGOUT
                // ====================================================

                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl(
                                "/login?logout=true"
                        )

                        .invalidateHttpSession(true)

                        .deleteCookies("JSESSIONID")

                        .permitAll()

                );


        // ========================================================
        // CONSTRUIR CONFIGURACIÓN
        // ========================================================

        return http.build();

    }

}

