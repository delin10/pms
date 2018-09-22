package pms.util.db.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pms.util.db.type.TypeDictionary;

@Target (ElementType.FIELD)
@Retention (RetentionPolicy.RUNTIME)
public @interface Column {
	public TypeDictionary type();
	public String name() default "";
	public int[] limit() default {};
	public boolean primary_key() default false;
	public boolean foreign_key() default false;
	public boolean not_null() default false;
	public  String refTable() default "";
	public String refProp() default "";
	
}
