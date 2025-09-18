package com.Satander.CQRS;

import java.util.List;

import java.util.Collections;
import redis.clients.jedis.Jedis;


public class Cache {

    private static final String HOST = "localhost";
    private static final int PORT = 6379;
    private static final String BALANCE_PREFIX = "account:balance:";
    private static final String HISTORY_PREFIX = "account:history:";

    public static Jedis jedis() {
        return new Jedis(HOST, PORT);
    }

    public void setBalance(String acc, String balance) {
        try (var j = jedis()) {
            if (j != null) {
                j.set(BALANCE_PREFIX + acc, balance);
            }
        } catch (Exception e) {
            System.err.println("Error setting balance for account " + acc + ": " + e.getMessage());
            java.util.logging.Logger.getLogger(Cache.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
        }
    }

    public void pushHist(String acc, String item) {
        try (var j = jedis()) {
            if (j != null) {
                j.lpush(HISTORY_PREFIX + acc, item);
            }
        } catch (Exception e) {
            System.err.println("Error pushing history for account " + acc + ": " + e.getMessage());
            java.util.logging.Logger.getLogger(Cache.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
        }
    }

    public String getBalance(String acc) {
        try (var j = jedis()) {
            return j != null ? j.get(BALANCE_PREFIX + acc) : null;
        } catch (Exception e) {
            System.err.println("Error getting balance for account " + acc + ": " + e.getMessage());
            java.util.logging.Logger.getLogger(Cache.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
            return null;
        }
    }

    public static List<String> getHist(String acc, int limit) {
        try (var j = jedis()) {
            return j != null ? j.lrange(HISTORY_PREFIX + acc, 0, limit - 1) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error getting history for account " + acc + ": " + e.getMessage());
            java.util.logging.Logger.getLogger(Cache.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
            return Collections.emptyList();
        }
    }
}