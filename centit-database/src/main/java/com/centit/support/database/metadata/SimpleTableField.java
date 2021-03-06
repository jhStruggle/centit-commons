package com.centit.support.database.metadata;

import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.common.JavaBeanField;
import com.centit.support.database.utils.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SimpleTableField implements TableField {
    private static final Logger logger = LoggerFactory.getLogger(SimpleTableField.class);

    private String propertyName;// 字段属性名称
    private String fieldLabelName;// 字段的中文名称 label ，PDM中的 Name 和 元数据表格中的Name对应
    private String columnType;// 数据库中的字段类型
    private String columnName;// 字段代码 PDM中的CODE
    private String columnComment;// 字段注释
    private String defaultValue;
    private String javaType;
    private boolean mandatory;
    private int     maxLength;//最大长度 Only used when sType=String
    private int  precision;//有效数据位数 Only used when sType=Long Number Float
    private int  scale;//精度 Only used when sType= Long Number Float
    private JavaBeanField beanField;

    public void mapToMetadata(){
        //这个和下面的 mapToDatabaseType 不对称
        propertyName = FieldType.mapPropName(columnName);
        javaType = FieldType.mapToJavaType(columnType,scale);
        if( ("Long".equals(javaType) || "Double".equals(javaType))
                && maxLength <= 0 )
            maxLength = 8;
        if( ("Date".equals(javaType) || "Timestamp".equals(javaType)
            || "sqlDate".equals(javaType) || "sqlTimestamp".equals(javaType))
                && maxLength <= 0 )
            maxLength = 7;
    }

    public String getHibernateType(){
        if(javaType !=null && ( javaType.equals("Date")|| javaType.equals("Timestamp")))
            return "java.util."+javaType;
        if("sqlDate".equals(javaType))
            return "java.sql.Date";
        if("sqlTimestamp".equals(javaType))
            return "java.sql.Timestamp";
        return "java.lang."+javaType;
    }

    public SimpleTableField()
    {
        mandatory = false;
        maxLength = 0;
        precision = 0;//有效数据位数 Only used when sType=Long Number Float
        scale = 0;//精度 Only used when sType= Long Number Float
    }
    /**
     * 字段属性名，是通过字段的code转化过来的
     * @return String
     */
    public String getPropertyName() {
        return propertyName;
    }
    public void setPropertyName(String name) {
        propertyName = name;
    }
    /**
     * 字段属性java类别
     * @return String
     */
    public String getJavaType() {
        return javaType;
    }



    public void setJavaType(String st) {
        javaType = FieldType.trimType(st);
    }

    public void setJavaType(Class<?> type) {
        javaType = ReflectionOpt.getJavaTypeName(type);
    }

    /**
     * 字段中文名，对应Pdm中的name
     * @return String
     */
    public String getFieldLabelName() {
        return fieldLabelName;
    }

    /**
     * 字段中文名，对应Pdm中的name
     * @param desc String
     */
    public void setFieldLabelName(String desc) {
        fieldLabelName = desc;
    }

    /**
     * 字段代码，对应Pdm中的code
     * @return String
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param  column 字段代码，对应Pdm中的code
     */
    public void setColumnName(String column) {
        columnName = column;
    }

    /**
     * 字段描述，对应Pdm中的Comment
     * @return String
     */
    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String comment) {
        columnComment = comment;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean notnull) {
        this.mandatory = notnull;
    }

    public void setMandatory(String notnull) {
        mandatory =
            ("true".equalsIgnoreCase(notnull) ||
                    "T".equalsIgnoreCase(notnull) ||
                    "Y".equalsIgnoreCase(notnull) ||
                    "1".equalsIgnoreCase(notnull));
    }

    public void setNullEnable(String nullEnable) {
        mandatory =
                ("false".equalsIgnoreCase(nullEnable) ||
                        "F".equalsIgnoreCase(nullEnable) ||
                        "N".equalsIgnoreCase(nullEnable) ||
                        "0".equalsIgnoreCase(nullEnable));
    }

    /**
     * 最大长度 Only used when sType=String
     * 这个和Precision其实可以共用一个字段
     * @return 最大长度
     */
    public int getMaxLength() {
        return maxLength;
    }
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * 有效数据位数 Only used when sType=Long Number Float
     * 这个和maxlength其实可以共用一个字段
     * @return 有效数据位数
     */
    @Override
    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }
    /**
     * 精度 Only used when sType= Long Number Float
     * @return 精度
     */
    @Override
    public int getScale() {
        return scale;
    }
    public void setScale(int scale) {
        this.scale = scale;
    }
    /**
     * 字段属性在数据库表中的类型
     * @return String
     */
    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String type) {
        if(type !=null){
            columnType = type.trim();
            int nPos = columnType.indexOf('(');
            if(nPos>0)
                columnType = columnType.substring(0,nPos);
        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setObjectField(Field objectField) {
        if(beanField==null)
            beanField = new JavaBeanField();
        beanField.setObjectField(objectField);
    }

    public void setObjectSetFieldValueFunc(Method objectSetFieldValueFunc) {
        if(beanField==null)
            beanField = new JavaBeanField();
        beanField.setSetFieldValueFunc(objectSetFieldValueFunc);
    }

    public void setObjectGetFieldValueFunc(Method objectGetFieldValueFunc) {
        if(beanField==null)
            beanField = new JavaBeanField();
        beanField.setGetFieldValueFunc(objectGetFieldValueFunc);
    }

    public void setObjectFieldValue(Object obj, Object fieldValue) {
        beanField.setObjectFieldValue(obj,fieldValue);
    }

    public Object getObjectFieldValue(Object obj) {
        return beanField.getObjectFieldValue(obj);
    }
}
