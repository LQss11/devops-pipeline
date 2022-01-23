import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("MyUSERNAME","MyPASSWORD")
instance.setSecurityRealm(hudsonRealm)
instance.save()

def strategy = new hudson.security.FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)
strategy.add(Jenkins.ADMINISTER, "myUSERNAME")
instance.setAuthorizationStrategy(strategy)