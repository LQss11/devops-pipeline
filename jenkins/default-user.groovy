import jenkins.model.*
import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.security.s2m.AdminWhitelistRule
import jenkins.model.Jenkins

def env = System.getenv()
def instance = Jenkins.getInstance()

// Initial username and password using env variables
// Setup Security
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(env.JENKINS_USER, env.JENKINS_PASS)
instance.setSecurityRealm(hudsonRealm)

// Enable Crumb issuer for CSRF protection
instance.setCrumbIssuer(new DefaultCrumbIssuer(true))

// Disable CLI Over Remoting
//instance.getDescriptor("jenkins.CLI").get().setEnabled(false)

// Enable Agent -> Master subsystem
instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

// Set the default URL
def jlc = JenkinsLocationConfiguration.get()
jlc.setUrl("http://localhost:"+env.JENKINS_PORT+"/")
jlc.save()

// associate the created user to become admin
def strategy = new GlobalMatrixAuthorizationStrategy()
strategy.add(Jenkins.ADMINISTER, env.JENKINS_USER)
instance.setAuthorizationStrategy(strategy)

// save current Jenkins state to disk
instance.save()