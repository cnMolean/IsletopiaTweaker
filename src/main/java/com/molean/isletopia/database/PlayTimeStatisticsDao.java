package com.molean.isletopia.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayTimeStatisticsDao {


    public static void checkTable() {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "create table if not exists playtime_statistics(\n" +
                    "    id int auto_increment primary key not null ,\n" +
                    "    player text not null ,\n" +
                    "    server text not null ,\n" +
                    "    startTimeStamp long not null,\n" +
                    "    endTimeStamp long not null ,\n" +
                    "    playtime long not null\n" +
                    ")";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void addRecord(String player, String server, long start, long end) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "insert into playtime_statistics(player, server, startTimeStamp, endTimeStamp, playtime) VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, server);
            preparedStatement.setLong(3, start);
            preparedStatement.setLong(4, end);
            preparedStatement.setLong(5, end - start);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static long getServerRecentPlayTime(String server, long start) {
        try (Connection connection = DataSourceUtils.getConnection()) {
            String sql = "select SUM(playtime) from playtime_statistics where server=? and startTimeStamp>?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, server);
            preparedStatement.setLong(2, start);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }
}
