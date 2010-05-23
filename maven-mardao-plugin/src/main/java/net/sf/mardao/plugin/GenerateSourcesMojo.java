package net.sf.mardao.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * @goal generate-sources
 * @author f94os
 *
 */
public class GenerateSourcesMojo extends AbstractMardaoMojo {
	
	private void mergeGeneric() throws ResourceNotFoundException, ParseErrorException, Exception {
		// GenericDao in target dao folder
		mergeTemplate("GenericDao.vm", targetDaoFolder, "GenericDao.java");
		
		// GenericDaoJpa implementation in src dao folder
		mergeTemplate("GenericDaoJPA.vm", srcDaoFolder, "GenericDaoJPA.java");
		
		// SingletonEntityManagerFactory in src dao folder
		if (false == containerManagedEntityManager) {
			mergeTemplate("SingletonEntityManagerFactory.vm", srcDaoFolder, "SingletonEntityManagerFactory.java");
		}
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		try {
			mergeGeneric();
		} catch (Exception e) {
			throw new MojoExecutionException(getClass().getSimpleName(), e);
		}
	}

}
