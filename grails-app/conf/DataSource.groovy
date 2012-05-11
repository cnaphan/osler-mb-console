dataSource {
	pooled = true
	driverClassName = "com.mysql.jdbc.Driver"
	dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "create-drop" // one of 'create', 'create-drop','update'
			url = "jdbc:h2:mem:devDb"
			driverClassName = "org.h2.Driver"
			dialect = "org.hibernate.dialect.H2Dialect"
			username = "sa"
			password = ""
		}
		hibernate {
			show_sql = false
		}
	}
	test {
		dataSource {
			dbCreate = "create-drop"
			url = "jdbc:h2:mem:testDb"
			driverClassName = "org.h2.Driver"
			dialect = "org.hibernate.dialect.H2Dialect"
			username = "sa"
			password = ""
		}
	}
	production {
		dataSource {
			dbCreate = "update"			
			url = "jdbc:mysql://fsa4.site.uottawa.ca/osler-mb-prod?useUnicode=yes&characterEncoding=UTF-8"
			username = "oslermbuser"
			password = "oslermbuser"
			properties {
				// Borrowed from http://grails.1312388.n4.nabble.com/Getting-exceptions-jdbc4-CommunicationsException-The-last-packet-successfully-received-from-the-servl-td2309458.html
				// to solve socket exception problem when MySQL is running > 8 hours idle
				maxActive = 50
				maxIdle = 25
				minIdle = 1
				initialSize = 1
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				numTestsPerEvictionRun = 3
				maxWait = 10000

				testOnBorrow = true
				testWhileIdle = true
				testOnReturn = true

				validationQuery = "select now()"
			}
		}
	}
}