package com.github.robertvolkmann.customrolesprovider;

import org.codelibs.spnego.SpnegoHttpURLConnection;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.ietf.jgss.GSSException;
import org.junit.Test;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PrivilegedActionException;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

public class CustomRolesProviderIT {

    private Object[] get_roles(InputStream inputStream) throws IOException {
        final XContentParser parser = JsonXContent.jsonXContent.createParser(inputStream);
        XContentParser.Token token;
        while ((token = parser.nextToken()) != null) {
            if (token == XContentParser.Token.FIELD_NAME && parser.currentName().equals("roles")) {
                return parser.list().toArray();
            }
        }
        return new String[0];
    }

    @Test
    public void should_return_admin_role_for_user_with_roles() throws LoginException, IOException, PrivilegedActionException, GSSException {
        String url = System.getProperty("elasticsearch.rest.url");
        SpnegoHttpURLConnection connection = new SpnegoHttpURLConnection("client", "userWithRoles@LOCALHOST", "password");

        connection.requestCredDeleg(true);
        connection.connect(new URL(url + "/_shield/authenticate"));

        assertThat(connection.getResponseCode(), is(HTTP_OK));
        assertArrayEquals(get_roles(connection.getInputStream()), new String[]{"role1", "role2"});
    }

    @Test
    public void should_return_no_role_for_user_without_roles() throws LoginException, IOException, PrivilegedActionException, GSSException {
        String url = System.getProperty("elasticsearch.rest.url");
        SpnegoHttpURLConnection connection = new SpnegoHttpURLConnection("client", "userWithoutRoles@LOCALHOST", "password");

        connection.requestCredDeleg(true);
        connection.connect(new URL(url + "/_shield/authenticate"));

        assertThat(connection.getResponseCode(), is(HTTP_OK));
        assertArrayEquals(get_roles(connection.getInputStream()), new String[0]);
    }
}
