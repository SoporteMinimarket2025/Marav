// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.security;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Usuario;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// ======================================================
// 🔐 CUSTOM USER DETAILS
// ======================================================
public class CustomUserDetails
        implements UserDetails {

    // ==================================================
    // 🔥 USUARIO ORIGINAL
    // ==================================================
    private final Usuario usuario;

    // ==================================================
    // 🔥 ID USUARIO
    // ==================================================
    private final Integer id;

    // ==================================================
    // 🔥 CONSTRUCTOR
    // ==================================================
    public CustomUserDetails(
            Usuario usuario
    ) {

        this.usuario = usuario;
        this.id = usuario.getId();
    }

    // ==================================================
    // 🔥 OBTENER ID
    // ==================================================
    public Integer getId() {

        return id;
    }

    // ==================================================
    // 🏪 NEGOCIO ID
    // ==================================================
    public Integer getNegocioId() {

        return usuario.getNegocioId();
    }

    // ==================================================
    // 👤 NOMBRE
    // ==================================================
    public String getNombre() {

        return usuario.getNombre();
    }

    // ==================================================
    // 🔐 ROL
    // ==================================================
    public String getRol() {

        return usuario.getRol();
    }

    // ==================================================
    // 📧 EMAIL
    // ==================================================
    public String getEmail() {

        return usuario.getEmail();
    }

    // ==================================================
    // 🔥 AUTHORITIES
    // ==================================================
    @Override
    public Collection<? extends GrantedAuthority>
    getAuthorities() {

        return List.of(

                new SimpleGrantedAuthority(
                        "ROLE_" + usuario.getRol()
                )

        );
    }

    // ==================================================
    // 🔐 PASSWORD
    // ==================================================
    @Override
    public String getPassword() {

        return usuario.getPassword();
    }

    // ==================================================
    // 📧 USERNAME
    // ==================================================
    @Override
    public String getUsername() {

        return usuario.getEmail();
    }

    // ==================================================
    // ✅ CUENTA NO EXPIRADA
    // ==================================================
    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    // ==================================================
    // ✅ CUENTA NO BLOQUEADA
    // ==================================================
    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    // ==================================================
    // ✅ CREDENCIALES VIGENTES
    // ==================================================
    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    // ==================================================
    // ✅ USUARIO HABILITADO
    // ==================================================
    @Override
    public boolean isEnabled() {

        return usuario.getEstado() != null
                && usuario.getEstado();
    }
}

