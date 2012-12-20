Настройка jboss 7

1. добавить в модули драйвер postgres

2. в standalone.xml добавить:
 <datasource jta="true" jndi-name="java:jboss/datasources/PostgresqlDS" pool-name="java:jboss/datasources/PostgresqlDS_Pool" enabled="true" use-java-context="true" use-ccm="true">
    <connection-url>jdbc:postgresql://localhost:5432/media_platform</connection-url>
    <driver>postgresql</driver>
    <security>
        <user-name>happy</user-name>
        <password>secret</password>
    </security>
</datasource>

<driver name="postgresql" module="org.postgres">
    <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
</driver>

3.добавить jndi-name java:jboss/infinispan/hibernate в настройках кэша в консоли jboss.
