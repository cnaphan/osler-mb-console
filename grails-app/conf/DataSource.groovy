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
			/*url = "jdbc:mysql://fsa4.site.uottawa.ca/osler-mb-dev?useUnicode=yes&characterEncoding=UTF-8"			
			username = "oslerdev"
			password = "oslerdev"*/
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
			dbCreate = "create-drop" // one of 'create', 'create-drop','update'
			url = "jdbc:mysql://fsa4.site.uottawa.ca/osler-mb-test?useUnicode=yes&characterEncoding=UTF-8"
			username = "oslerdev"
			password = "oslerdev"
		}
	}
	production {
		dataSource {
			dbCreate = "update"			
			url = "jdbc:mysql://fsa4.site.uottawa.ca/osler-mb-prod?useUnicode=yes&characterEncoding=UTF-8"
			username = "oslermbuser"
			password = "oslermbuser"
		}
	}
}