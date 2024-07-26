package aws.databases;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Parameters;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.sql.*;
import java.util.Properties;

public class SSHMySQLConnector extends AbstractTest{

    //private static final String DB_URL = "jdbc:mysql://cloudximage-databasemysqlinstanced64026b2-b5g8ybgusre5.crgam0u8sl3j.eu-central-1.rds.amazonaws.com:3306/cloudximages";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cloudximages";
    private static final int LOCAL_PORT = 3306; // Local port for SSH tunnel
    private static final int REMOTE_PORT = 3306; // MySQL port on the remote server
    private static final Logger log = LoggerFactory.getLogger(SSHMySQLConnector.class);
    private ServerSocket serverSocket;
    private LocalPortForwarder forwarder;
    //private Connection conn;
    Connection conn = null;

    private Connection getConnection() throws SQLException {

        Properties connectionProps = new Properties();
        connectionProps.put("user", DB_USER_NAME);
        connectionProps.put("password", DB_USER_PASSWORD);

        return DriverManager.getConnection(DB_URL, connectionProps);
    }

    protected ImageMetadata getImageMetadataFromRds(String imageName) throws IOException, SQLException {
        SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());

        try {
            ssh.connect(PUBLIC_IP);
            KeyProvider keys = ssh.loadKeys(PATH_TO_PEM_FILE);
            ssh.authPublickey("ec2-user", keys);


                // Set up port forwarding from local port to remote MySQL port
                serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress("localhost", LOCAL_PORT));
                Parameters params = new Parameters("localhost", LOCAL_PORT, DB_INSTANCE_IDENTIFIER, REMOTE_PORT);
                forwarder = new LocalPortForwarder(ssh.getConnection(), params, serverSocket,  ssh.getTransport().getConfig().getLoggerFactory());

                // Start the port forwarding in a separate thread
                Thread forwarderThread = new Thread(() -> {
                    try {
                        forwarder.listen();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                forwarderThread.start();
            // Now you can connect to the MySQL database using the local port
            conn = getConnection();
                String query = "SELECT * FROM images i WHERE i.object_key = 'images/7936d654-6fc0-4b56-a1c0-97969c08a939-ES_v1_configuration.jpg'";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                   // stmt.setString(1, imageName);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return new ImageMetadata(
                                    rs.getString("id"),
                                    rs.getString("object_key"),
                                    rs.getLong("object_size"),
                                    rs.getString("object_type"),
                                    rs.getTimestamp("last_modified"));

                        }
                    }
                }

             catch (SQLException e) {
                log.error("SQL Exception: ", e);
                e.printStackTrace();
            }
        } finally {
            // Close database connection
            if (conn != null && !conn.isClosed()) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Error closing connection: ", e);
                }
            }
            // Close port forwarder
            if (forwarder != null) {
                try {
                    forwarder.close();
                } catch (IOException e) {
                    log.error("Error closing forwarder: ", e);
                }
            }
            // Close server socket
            if (serverSocket != null ) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    log.error("Error closing server socket: ", e);
                }
            }
            ssh.disconnect();
        }
        return null;
    }
    @Test
    public void rdsConnectionTest() {
        SSHMySQLConnector connector = new SSHMySQLConnector();
        try {
            ImageMetadata metadata = connector.getImageMetadataFromRds("ES_v1_configuration.jpg");
            if (metadata != null) {
                System.out.println(metadata);
            } else {
                System.out.println("No metadata found for the given image key.");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
