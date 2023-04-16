package com.example.research.infra;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@MappedTypes(UUID.class)
public class UUIDTypeHandler implements TypeHandler<UUID> {

    @Override
    public void setParameter(PreparedStatement ps, int i, UUID uuid, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, uuid);
    }

    @Override
    public UUID getResult(ResultSet rs, String column) throws SQLException {
        return rs.getObject(column, UUID.class);
    }

    @Override
    public UUID getResult(ResultSet rs, int i) throws SQLException {
        return rs.getObject(i, UUID.class);
    }

    @Override
    public UUID getResult(CallableStatement cs, int i) throws SQLException {
        return cs.getObject(i, UUID.class);
    }
}
