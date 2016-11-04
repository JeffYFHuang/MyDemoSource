package storm.cookbook.services;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;

import storm.cookbook.services.resources.CassandraUtils;
import storm.cookbook.services.resources.LogCount;


@ApplicationPath("/")
public class LogServices extends Application {
	
	public LogServices(){
		CassandraUtils.initCassandra();
	}
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register root resource
        classes.add(LogCount.class);
        return classes;
    }
}