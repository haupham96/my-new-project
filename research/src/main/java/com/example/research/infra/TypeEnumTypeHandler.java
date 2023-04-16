package com.example.mybatis.infra;

import com.example.mybatis.enums.Type;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Type.class)
public class TypeEnumTypeHandler implements TypeHandler<Type> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Type type, JdbcType jdbcType) throws SQLException {
        ps.setString(i, type.name());
    }

    @Override
    public Type getResult(ResultSet rs, String column) throws SQLException {
        String type = rs.getString(column);
        if (type == null) return null;
        return Type.valueOf(type);
    }

    @Override
    public Type getResult(ResultSet rs, int i) throws SQLException {
        String type = rs.getString(i);
        return Type.valueOf(type);
    }

    @Override
    public Type getResult(CallableStatement cs, int i) throws SQLException {
        String type = cs.getString(i);
        return Type.valueOf(type);
    }

}
