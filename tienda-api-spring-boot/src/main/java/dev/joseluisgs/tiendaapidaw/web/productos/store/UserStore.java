package dev.joseluisgs.tiendaapidaw.web.productos.store;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

// La anotación
//@Scope con estos
//valores hace que
//haya un componente
//por cada usuario
@Component("userSession")
@Scope(value = WebApplicationContext.SCOPE_SESSION,
        proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserStore {
    @Getter
    @Setter
    private boolean logged = false;
    @Getter
    private int loginCount = 0;

    @Getter
    @Setter
    private Date lastLogin;

    public void incrementLoginCount() {
        loginCount++;
    }

}