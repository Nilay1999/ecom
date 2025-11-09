@echo off
echo Running Data Seeder...

mvn exec:java -Dexec.mainClass="com.example.ecommerce.SeedDataScript" -Dspring.profiles.active=local

pause
