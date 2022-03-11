package io.aomsweet.issue;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.concurrent.TimeUnit;

/**
 * @author aomsweet
 */
public class VertxPgPool {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(5432)
                .setIdleTimeout(1)
                .setIdleTimeoutUnit(TimeUnit.MINUTES)
                .setTcpKeepAlive(true)
                .setHost("127.0.0.1")
                .setDatabase("postgres")
                .setUser("postgres")
                .setPassword("postgres");

        PoolOptions poolOptions = new PoolOptions()
                .setIdleTimeout(1)
                .setIdleTimeoutUnit(TimeUnit.MINUTES)
                .setConnectionTimeout(1)
                .setConnectionTimeoutUnit(TimeUnit.MINUTES)
                // MaxWaitQueueSize
                .setMaxWaitQueueSize(Integer.MAX_VALUE)
                // MAXSIZE
                .setMaxSize(8);

        // Create the client pool
        PgPool pool = (PgPool) PgPool.client(vertx, connectOptions, poolOptions);

        // Concurrent open transactions
        for (int i = 0; i < 10; i++) {
            pool.withTransaction(conn -> {
                System.out.println(conn);
                return Future.succeededFuture();
            }).onFailure(Throwable::printStackTrace);
        }
    }
}
