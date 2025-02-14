# GHAS Security Demo Walkthough

Once you have a provisioned demo repository, wait for the initial GitHub Actions workflows to complete and you will have a starting state of the repository with the following starting features:
* One Dependabot dependency vulnerability on JUnit (it may not apprear immediately and require another commit on the `main` branch, but will show up in the process of the demo flow
* A container with our Jetty standalone application that will serve the bookstore to users on port `8080` published to the GitHub Container Registry
* A number of Code Scanning vulnerability results related to our container image
* A Google API key secret detected in the commit history
* A `prod` deployment of the container as an Azure Web Application


There is a recording of this demo repository available at both GitHub and Microsoft where @peter-murray and @nickliffen take a guide approach through this demo and add some extra details and comments about the features and findings.

* GitHub rewatch: https://github.rewatch.com/video/r09q3rrdkskaqexy-ghas-complete-e2e-features-customer-demo
* Microsoft Stream: https://msit.microsoftstream.com/video/6aae0840-98dc-bd6c-8c84-f1ecf5706416


## Demo Narrative Flow

This demo is built to address a customer perspective of bringing GHAS to a repository, activating all the features and then starting a journey to process the various findings and remediate these.

### Fixing Code Scanning Vulnerabilities
The container is being built and scanned as part of the [`.github/workflows/build_test_publish.yml`](../.github/workflows/build_test_publish.yml) workflow. It is leveraging a GitHub Actions reusable workflow that has been provided centrally by our DevSecOps team and if we can successfully build and test our application, the jar file is passed to this workflow to create a container for us.
The reusable workflow encapsulates the approved process and scanning that is required for us to be compliant with building containers and will register any detected vulnerabilities as `Code scanning alerts` on our repository.
This workflow also contains a signing of our containers, so that an audit trail can be inspected and the source and workflow that generated the container is stored as part of the signature.


1. Navigate to the `Security` tab on the repository
  ![Screenshot 2022-06-26 at 13 16 37](https://user-images.githubusercontent.com/681306/175813691-2fd31941-ac55-42a1-957f-5b1f321c8b41.png)


1. Select the `Code scanning alerts`
  ![Screenshot 2022-06-26 at 13 15 21](https://user-images.githubusercontent.com/681306/175813639-1c0826aa-a363-4a3f-bd4e-0af9f2951047.png)


1. Open the first finding it should be `GHSA-26vr-8j45-3r4w high vulnerability for jetty-io package`
  ![Screenshot 2022-06-26 at 13 17 40](https://user-images.githubusercontent.com/681306/175813740-029c02bf-87b4-4bda-b825-fb08bd9f5e48.png)


1. Expand the `Show more` section within the finding to show the Anchor container scanning detection data.
  ![Screenshot 2022-06-26 at 13 17 52](https://user-images.githubusercontent.com/681306/175813784-37cf57fe-90cb-4bf8-ba08-6a9a16ff7152.png)


1. From here we can see the necessary fix version `10.0.2` of the jetty package as well as a link th the GitHub Security Advisory Database finding that was raised for this vulnerability, You can click on the linked GHSA to see more details as to the finding, including the vulnerable versions, fix version and a full description, work arounds and external references to find more data about this finding if needed.
  ![Screenshot 2022-06-26 at 13 18 07](https://user-images.githubusercontent.com/681306/175813935-64d50f16-f967-49d3-98f2-a78f6202c534.png)


1. Now we need to remediate this finding by updating our version of Jetty in our project. Navigate to the `Code` tab on the repository and then click on the green `<> Code` button and select `Codespaces` within the popup, click `Create codespace on main` to bring up our Codespace.
  ![Screenshot 2022-06-26 at 13 24 11](https://user-images.githubusercontent.com/681306/175814028-6a25c6b3-3cb6-4847-be33-2b0d4b8ce3db.png)


1. The Codespace will start to be built for you, which can take around 30 seconds to provision it
   ![Screenshot 2022-06-26 at 13 26 15](https://user-images.githubusercontent.com/681306/175814053-ad9cf802-52c2-4efa-8c2d-d6c1041a83ff.png)
   * The Codespace is configured to utilize a 4CPU and 8GB memory VM that will have all the extensions configured and built into it. Once the Codespace starts, it may take another 20 seconds for all the extensions to install and activate for use.
   
   * Do not be concerned by the following error if it pops up, it is the extension activating before the Java project extention activates and it is not of any consequence (we cannot control the ordering of the activations of the extensions unforntunately), you can click the `Install and Reload` button, which will restart the Codespace, but you can also just dismiss it for now.
    ![Screenshot 2022-06-26 at 13 28 40](https://user-images.githubusercontent.com/681306/175814202-3b3e230b-43f0-46a6-89f2-96b24a9dec03.png)
   
   * The sign that we are fully ready to go is once you see the `Projects are imported into workspace`
    ![Screenshot 2022-06-26 at 13 28 46](https://user-images.githubusercontent.com/681306/175814233-d9909f69-142e-492c-a2d8-50fb7b8174ae.png)


1. Now that you have a Codespace up, there are number of VSCode Tasks built into the Codespace to make demoing easier. These are available by activating the `Command Palette...` either using the shortcut for your OS (`Shift + Command + P` for MacOS) or from the Hamburger menu

    ![Screenshot 2022-06-26 at 13 34 32](https://user-images.githubusercontent.com/681306/175814479-8425345d-d770-49a3-9f87-efa23655c661.png)


1. In the `Command Palette` type `Run Task` and select the `Tasks: Run Task` option and then select the prebuilt task you wish to run. In this case we have a `security: update to Jetty 10.0.2` task to run which will apply a prebuilt patch set to modify the dependencies in the `pom.xml` to update Jetty for us.

   ![Screenshot 2022-06-26 at 13 38 09](https://user-images.githubusercontent.com/681306/175814853-98c9174f-9e39-45dc-98d1-0f7a415dfd55.png)
    
   * When the patch set is applied, you will see an output that looks like this in the terminal
    ![Screenshot 2022-06-26 at 13 38 20](https://user-images.githubusercontent.com/681306/175814888-38684dfd-9254-490e-a55b-6230ad76c10c.png)


1. Commit the changes and push them to GitHub, you can do this via the terminal or alternatively navigate to the `Source Control` where you can inspect and show the changes to the `pom.xml` and then commit the file using any commit message you like, I used `Fixing GHSA-26vr-8j45-3r4w` as that is the detected vulnerability.
   ![Screenshot 2022-06-26 at 13 47 25](https://user-images.githubusercontent.com/681306/175815027-96bd5f37-9d07-4e7e-ae1c-b6fcb1b8af46.png)
    
   * Then push the changes to GitHub using the `Sync Changes` button
   
      ![Screenshot 2022-06-26 at 13 50 23](https://user-images.githubusercontent.com/681306/175815031-7358cda6-312c-4f40-a7a1-c3e4c8e6f614.png)


1. At this point we can navigate back to the reposiotry and select the `Actions` tab to view the GitHub Actions workflows that are executing due to us pushing those changes directly to the `main` branch
   ![Screenshot 2022-06-26 at 14 07 10](https://user-images.githubusercontent.com/681306/175815595-5ac9ae68-30af-4ada-bace-fabc8e99e93a.png)
   
   * There are two workflows that will trigger `Build - Test - Publish` (our CI/CD workflow) and `Code scanning` (for CodeQL scanning). The one we are interested in is the `Build - Test - Publish` which will re-run the container scan based off our changes to Jetty and will trigger a `Container Vulnerability Scanning` workflow once the container is built.
    ![Screenshot 2022-06-26 at 14 08 15](https://user-images.githubusercontent.com/681306/175815732-51bf678e-801d-4aea-af8a-a3a167b50000.png)
    
   * This repository performs Continuous Delivery for any commits on the `main` branch which will result a deployment to our `prod` environment as well as the more important `Container Vulnerability Scanning` workflow which is responsible for running Anchor container scanner and publishing the results.
     ![Screenshot 2022-06-26 at 14 08 58](https://user-images.githubusercontent.com/681306/175815834-00d00abc-e1dc-46f1-8d17-ef6f2df8ed9a.png)
     
   * Wait for the `Container Vulnerabilty Scanning` workflow to complete the middle step (`container_scan / Anchore`) and once that is completed the new security findings on the container will have been published back to the repository under the `Security` tab
    ![Screenshot 2022-06-26 at 14 14 01](https://user-images.githubusercontent.com/681306/175815916-120aa320-ec34-4214-b5fe-63342b5173fb.png)
    

1. Navigate to the `Security` tab and select the `Code scanning alerts`, you should see the number has dropped from what it is before. There is now a number of `Closed` alerts in the table header, but we are not completely out of the woods yet, there are still alerts...
   ![Screenshot 2022-06-26 at 14 16 30](https://user-images.githubusercontent.com/681306/175816044-07b0ef66-beb4-4667-a50d-cffa7ea5b30f.png)


1. Select the first alert it should be related to `CVE-2021-34429`, there are a few of them, and any one will do. Expand the `Show more` section to see what was detected.
  ![Screenshot 2022-06-26 at 14 19 16](https://user-images.githubusercontent.com/681306/175816110-bf09f416-c516-45ae-9593-cb140ea015e1.png)

   * This time we do not have the `Fix Version` data present in the finding, so we will need to follow the `Link` to get more information to remediate this alert.
     ![Screenshot 2022-06-26 at 14 22 05](https://user-images.githubusercontent.com/681306/175816179-d31c17fe-5138-4439-9219-34e441ef78fb.png)
   
   * Scroll down on the page to the `Hyperlink` section and click on the `GHSA` link
     ![Screenshot 2022-06-26 at 14 22 05](https://user-images.githubusercontent.com/681306/175816221-4488fcd7-b5b4-4858-95c6-f5a92f5f9760.png)
     ![Screenshot 2022-06-26 at 14 22 13](https://user-images.githubusercontent.com/681306/175816230-28fd277d-dba3-443f-ab2e-091eb5d9597d.png)

   * Upon following the link, we now have the necessary information to understand the alert and how to remdiate this, which it to upgrade to Jetty version `10.0.6`
      ![Screenshot 2022-06-26 at 14 23 45](https://user-images.githubusercontent.com/681306/175816282-ddc6df5b-64d7-4457-a67c-62f092c2c323.png)


1. Go back to the Codesapce and run the next `security: update to Jetty 10.0.6`, commit the changes and wait for the workflows to complete as per the above process for the Jetty `10.0.2` update.
   ![Screenshot 2022-06-26 at 14 25 33](https://user-images.githubusercontent.com/681306/175816582-676068fd-5756-4ff9-9f83-1d3195eaabda.png)
   
   ![Screenshot 2022-06-26 at 14 26 10](https://user-images.githubusercontent.com/681306/175816589-e95d4253-3814-476e-8a3b-a79f1db0f4e8.png)


1. Upon completion of the `Container Vulnerability Scanning` we should be met with no Open Code Scanning alerts (only Closed ones) on the repository.
   ![Screenshot 2022-06-26 at 14 51 44](https://user-images.githubusercontent.com/681306/175817461-191f9e2a-81b3-4ea3-ab53-ff035494b123.png)
   
   * Note that we did not need to do anything manually to close these out, just remediating the underlying vulnerabile version of the `Jetty` server dependencies resulted in the vulnerabilites getting closed out as per our updated container scanning results :tada:.


### Coding a feature

Now that our Code Scanning results are clear, we can focus back on delivering business requirements in our application. Our users have been asking for some search functionality for our bookstore, so let us proceed with building out that using a GitHub Flow process that will create a feature branch and allow us to iterate and perform code review and validation of our new feature before pushing these to our `prod` environment.

1. Return to your Codespace that you had open. If you did any other commits, ensure that you are up to date with any code changes that were applied outside the Codespace (following this narrative flow there will not be any).

1. Starting from the `main` branch inside the Codespace once again utilize the `Tasks: Run Task` to execute the `demo: code feature` task.

    ![Screenshot 2022-06-26 at 14 59 40](https://user-images.githubusercontent.com/681306/175817809-42f17125-9d5f-4ea8-848a-66935fd5a0d1.png)

    * This task will prompt you for some extra inputs to select the right feature, select the default `book search`
    
       ![Screenshot 2022-06-26 at 14 59 48](https://user-images.githubusercontent.com/681306/175817846-db930ddf-a8cf-4412-ad01-4e775eab199f.png)

    * Finally provide a branch name for the feature, this is optional as just pressing enter and accepting the default will result in a branch name of `feature-book-search`, but if had already applied this demo feature already, you can provide a new alternative name, as the branch must not already exist.
    
       ![Screenshot 2022-06-26 at 14 59 56](https://user-images.githubusercontent.com/681306/175817915-a82a0c05-4d23-4c98-808b-802bd3a32b92.png)

1. This time the files that are changed are all source code files, The main entry point for our applicaiton the `DemoServer.java` and two Servlets `BookApiServlet.java` (provides a REST endpoint) and `BookServlet.java` (the servlet used to serve our frontend HTML). You can add a commit messsage, and then push this new feature branch to the GitHub repository
   ![Screenshot 2022-06-26 at 15 10 13](https://user-images.githubusercontent.com/681306/175818246-85d16e2d-b576-4cc8-b1fc-f8c598fbadb6.png)


1. Once the feature branch is pushed, you will see a popup in the Codespace asking if you want to create a Pull Request
   ![Screenshot 2022-06-26 at 15 10 47](https://user-images.githubusercontent.com/681306/175818322-645e2dd9-43ff-450a-99d5-36bd965fd458.png)
   
   * You can create the Pull Request inside the Codespace, or alternatively (as I prefer) navigate back to the repository and use the prompts from the detected new branch that was pushed to create the Pull Request from the GitHub repository
      
      ![Screenshot 2022-06-26 at 15 11 13](https://user-images.githubusercontent.com/681306/175818390-60733945-60a0-404a-ac84-4f1765fba9d5.png)
      
      ![Screenshot 2022-06-26 at 15 11 29](https://user-images.githubusercontent.com/681306/175818410-0ef2584a-d999-4e92-9f54-f2486e9c3666.png)

   
1.  Once the Pull Request is opened (if you did it from the Codepsace you will need to navigate to it on the repository) and you will see a number of Status Checks starting to execute (these are from GitHub Actions) to validate and check our code changes as they would apply to the `main` branch if merged.

   ![Screenshot 2022-06-26 at 15 11 44](https://user-images.githubusercontent.com/681306/175818483-5a29d29a-c140-4fd9-a483-6a2fb71a99b7.png)
   
   * You can see that a number of these are marked as required, these are specified in our branch protection rules on the repository so that we can properly quality gate our changes and enforce best practices when updating our source code.
   

1.  If you wait a short time (less than a minute) the `CodeQL` workflow will complete fairly quickly and we will see that it has detected two security vulnerabilities as per our CodeQL query configuration in the workflow []()
   ![Screenshot 2022-06-26 at 15 17 45](https://user-images.githubusercontent.com/681306/175818652-02f6d52f-ec92-4356-bd31-e03b418030a1.png)


1. Click the `Details` on the failed status check to navigate to the summary of the findings
   ![Screenshot 2022-06-26 at 15 18 06](https://user-images.githubusercontent.com/681306/175818697-a12fdc21-c594-4a79-a740-e22b5f81a73f.png)


1. Focusing on the first one, `Query built from user-controlled sources` click on the `Show more details` link to take use to the resulting `Code scanning alert` that was generated for this
   ![Screenshot 2022-06-26 at 15 22 42](https://user-images.githubusercontent.com/681306/175818823-ccd35dd5-fbd0-4e2a-baad-12ba4fa18ee3.png)

   * You can see that there is a badge indicating this finding is from a Pull Request and then name of the merge branch that GitHub generated for the PR in which the vulnerability exists
   
   * There is a code snippet shown where the sink of the CodeQL vulnerability was detected, alone with a `Show paths` link to be able to show all code paths that lead to this vulnerability. In this case we have two paths, both of the Servlets manage to lead to the same sink
   
      ![Screenshot 2022-06-26 at 15 26 50](https://user-images.githubusercontent.com/681306/175819025-89d1c977-35b6-497d-8e76-d3055e9d0e48.png)
      
      Select the file drop down to reveal the second path:
      
      ![Screenshot 2022-06-26 at 15 26 57](https://user-images.githubusercontent.com/681306/175819033-8128f653-0010-49a8-9617-46b1174154da.png)

   * Clicking the `Show more` on the alert will provide a lot more context to the actual alert, what has been detected, and information showcasing examples (good and bad) along with any other references to external data that can be used to better understand and learn about the detected finding.
     This is where CodeQL alerts in particular (compared to non GitHub thrid party security integrations) contain many resources so that they can utilized as training/learning aids so developers can built up knowledge and potentially avoid making the same errors in the future.  

      ![Screenshot 2022-06-26 at 15 24 32](https://user-images.githubusercontent.com/681306/175819296-38b58ff9-4a1f-4be9-b215-22ed2db59bff.png)


1. Right, so with an understanding that we need to protect from this potential for an attacker to hit our database with a string that they control, we need to remedy this in our feature branch. Return to your Codepsace and once again `Tasks: Run Task` and select the `security: fix search SQL vulnerability`

   ![Screenshot 2022-06-26 at 15 35 34](https://user-images.githubusercontent.com/681306/175819474-1e5a6f2b-e311-463f-bb90-196d8b312700.png)
   
   * Note this will unpack the changes directly into the existing branch (not creating one like the actual implementation of the feature did), make sure you have not changed branches in your Codespace before fixing this vulnerability


1. The fix will be a modification to the `BookDatabaseImpl.java` file to introduce a Prepared Statement for accessing the database, which will prevent unsantized strings from the user hiting the database. This fix will resolve the issue for both detected code paths in the alert.

   ![Screenshot 2022-06-26 at 15 36 48](https://user-images.githubusercontent.com/681306/175819630-4579bfe6-3eed-45ef-bf17-4f4358de0db3.png)

   * Commit the change and ten push it back to the feature branch so that the Pull Request will update


1. Navigate back to the Pull Request and all the commit status checks will have been cleared and the GitHub Actions workflows all starting to run again now that there are new changes to build and validate.
   ![Screenshot 2022-06-26 at 15 41 21](https://user-images.githubusercontent.com/681306/175819744-c9e98c42-0c69-45eb-949a-fa651d850fe5.png)
   
   * The `Code scanning results / CodeQL` will still fail, but now there will only be the one alert, for `Log injection`.
   
   * Click on the `Details` for the failing check in the PR and then click the `Dismiss alert` providing a reason as to why we are not fixing this
      
      ![Screenshot 2022-06-26 at 15 45 37](https://user-images.githubusercontent.com/681306/175819910-ee432fa8-cbd3-469c-b54f-115df45d6536.png)
      
   * The dismissal forms part of an audit trail on the alerts for the repository and can be reviewed and potentially reopened at a point in the future is deemed necessary to resolve


1. Now that the alert has been cleared, navigate back to the Pull Request and we should see that all the check are now passing
   ![Screenshot 2022-06-26 at 15 48 24](https://user-images.githubusercontent.com/681306/175820044-ccdd0739-7bf1-4abb-af3c-279bbd073cce.png)

   * The outstanding thing to merge this change into `main` is a code review. If you are a GitHub employee and have staff mode active then you will be able to use the `/pr` slash command to invoke a Pull Request Review from our GitHub Application to clear/satisfy that requirement. This is a nice to have if you are going to merge this feature into the `main` branch, but at this point is not required to showcase the feature.


### Secret scanning alerts

On the repository there is a Google API key committed to the repository as the initial commit.

1. Navigate to the `Security` tab on the repository and select the `Secret scanning alerts`
   ![Screenshot 2022-06-26 at 15 51 38](https://user-images.githubusercontent.com/681306/175820197-e48a97e4-ae3f-4eb1-88ad-ce1c3fd46e28.png)


1.  Select the detected secret to get more infomation
   ![Screenshot 2022-06-26 at 15 53 40](https://user-images.githubusercontent.com/681306/175820233-81d571c6-cf4d-4118-8a68-b6f4f6580e54.png)

   * From here you can see the file and snippet of where it was detected, along with the commit and user that made the commit
   
   * There is also a suggestion on how to remediate the secret. GitHub recommends that users consider the secret completely compromised at this point and take action to revoke the secret to prevent any mallicious or unintended use of it. https://docs.github.com/en/enterprise-cloud@latest/code-security/secret-scanning/managing-alerts-from-secret-scanning#securing-compromised-secrets
   
   * If the secret is valid, some users might want to remove the secret from the commit history of the repository. This is potentially error prone, requires a lot of effort and care to ensure once taken out it does not get accidententally readded from an old commit. Also it will effectively result in your commit histoy, i.e. SHAs being modified for all commits after the one that added the secret, which may break any audit and tracing that was once valid to the old history.

1. In this case we can record a remediation of `Revoked` as this secret has already been revoked by our company, which will close out this finding.
    
    ![Screenshot 2022-06-26 at 16 01 47](https://user-images.githubusercontent.com/681306/175820572-16a75569-d559-4d0f-9b1e-62641b212bae.png)
    
    ![Screenshot 2022-06-26 at 16 01 56](https://user-images.githubusercontent.com/681306/175820576-7f6474fd-ba9b-4749-86c9-bff3586ebad9.png)
    
    * The detection can be reopened if a mistake was made, or it was not properly revoked
       ![Screenshot 2022-06-26 at 16 03 49](https://user-images.githubusercontent.com/681306/175820605-2edeb8cd-6cb4-45bd-b48d-0ec3775dc9c5.png)

    * Also the user and time of the remediation is recorded as part of an audit trail


### Secret Push Protection

Speaking of secrets... What about the potential of securing our code base from secrets actually managing to make it into the code base to begin with so that we do not have to go through this remediation process.

That is what [Secret scanning Push protection](https://docs.github.com/en/enterprise-cloud@latest/code-security/secret-scanning/protecting-pushes-with-secret-scanning) is for and we have it enabled on our repository!

1. Return to your Codesapce and ensure you are on a branch that is up to date and you can push from.


1. Once again `Tasks: Run Task` has a task called `security: inject secrets` which will add a file containing a secret that we can commit to our respository
    
    ![Screenshot 2022-06-26 at 16 08 59](https://user-images.githubusercontent.com/681306/175820822-a191855f-e0f8-4a3d-b0a6-7b8fb1176802.png)

    ![Screenshot 2022-06-26 at 16 10 34](https://user-images.githubusercontent.com/681306/175820874-4086a9d9-8f1b-4bad-96bc-a62046e6e793.png)


1. Add the file and commit it locally
    ![Screenshot 2022-06-26 at 16 11 32](https://user-images.githubusercontent.com/681306/175820903-042d11e9-2fda-42f9-a16b-05c1c7f17972.png)


1. Now try to push the secret (I prefer to use the command line on the Terminal for this)

   ![Screenshot 2022-06-26 at 16 13 04](https://user-images.githubusercontent.com/681306/175820971-ca52b5fc-b22f-4908-9705-3e214b830c1e.png)
   
   * Secret scanning push protection has blocked the push, to prevent us leaking the detected secret (and having to revoke it). It provides details on the actual detection, the commit and the file and line within that commit so that it can bre checked and remediated
   
   * If the secret is a false positive, or one that is valid for your use case to push, then you can override the prevention mechanism by following the link in the message where you can register a reason for allowing it (which will form part of an audit trail)
   
      ![Screenshot 2022-06-26 at 16 16 03](https://user-images.githubusercontent.com/681306/175821094-d387372e-507d-46c8-a0df-ecca5e54f033.png)

   * You can fix the local commit if desired, removing the secret from the commit history and then push your changes successfuly once the commit is expunged `git reset HEAD^` will remove that last commit for example.


### Dependabot Vulnerabilities

At this point there is just one more security alert on our repository which is coming from a vulnerable dependency.

1. Navigate to the `Security` tab and select `Dependabot alerts`
   ![Screenshot 2022-06-26 at 16 16 03](https://user-images.githubusercontent.com/681306/175821297-9dd59f3b-ce34-4fe4-a0be-2542b55e14ad.png)


1. Select the alert and from there you will be presented with the context for the detection along with any remediations or versions that contains patches to solve the alert.
   ![Screenshot 2022-06-26 at 16 22 11](https://user-images.githubusercontent.com/681306/175821362-a56db924-0c48-48e0-8f6f-42dad9f5b2d8.png)


1. In the case of this JUnit vulnerability, there is a known patched version and dependabot has helpfully created a Pull Request for us already with the fix in it. Click the `Review security update` green button to open the PR
   ![Screenshot 2022-06-26 at 16 25 18](https://user-images.githubusercontent.com/681306/175821466-63b517e6-c1cd-4a63-96da-b05f59a1ff9f.png)


1. If we scroll down to the status checks on the Pull Request, we can see that some GitHub Actions workflows have not executed, but this particular change did pass our `Build and Test` giving us confidence that our source code is compiling and the tests are still passing.
   ![Screenshot 2022-06-26 at 16 25 32](https://user-images.githubusercontent.com/681306/175821525-bc0df914-d9ca-44a4-8791-e1b990784f84.png)


1. Due to concerns about supply chain poisoning or hijacking, we do not trust dependabot created Pull Requests and treat it like an external party creating a fork ase Pull request on our repository. As such it has no access to any Actions Secrets on our repository, organization or environments. But for us to publish a container, we need to authenticate with `ghcr.io`. This means we cannot pass these checks as the will not run on our respository, so we would have to use our Admin privileges to override an merge the Pull Request

   * It is up to customers to decide on what level of sensitivity that they view Dependabot Pull Requests having access to GitHub Actions secrets, and they can allow this, but it is off by default on the repository
   
   * Some package management systems are more susceptable to these types of attacks that others, npm is a stand out one for this due to the installation scripts on some packages for instance (you can disable this by the way, but your milage will vary depending upon the packages you use)...


1. If you merge the PR, then this will finally close out all the alerts on the repository and you have a clean slate to start working from 🎉


## Extra credit demoing

There is another potential demo opportunity on this repository, which will introduce a Log4j vulnerability into the application, as well as give you an opportunity to demo the vulnerability in action.

_Note: this feature of the demo, will potentially reset some code changes if you have followed to demo steps above, this is because it will modify some of the files we have lready remediated._

1. Open a Codespace on the repository and either create a new branch, or use an existing one that you can push to GitHub on.


1. We have a task for that is in operation again... `Tasks: Run Task` and select `security: log4j vulnerability` to apply the changes to the current branch
   
   ![Screenshot 2022-06-26 at 16 39 06](https://user-images.githubusercontent.com/681306/175822170-fbe221a7-a2f3-4718-9702-dccd39d3e60d.png)

   * The `pom.xml` will be modified to use `2.14.1` of log4j
   
      ![Screenshot 2022-06-26 at 16 41 27](https://user-images.githubusercontent.com/681306/175822243-94d8e1e6-7e5b-4356-aa94-86397debf6d3.png)

   * The `StatusServlet.java` will gain a `ThreadContext` that will store an API version that the user can request when accessing the status page
   
      ![Screenshot 2022-06-26 at 16 42 47](https://user-images.githubusercontent.com/681306/175822312-3d1f5618-1ea9-41ff-bd1a-087506884d5a.png)
      
   *  The `log2j2.xml` will be modified to output the `api-version` in the log statements

       ![Screenshot 2022-06-26 at 16 44 36](https://user-images.githubusercontent.com/681306/175822360-f9405e22-86d6-4d89-9fcc-8d17fde849fa.png)


1. Commit the changes and push them up to GitHub so that the GitHub Actions workflows can start to validate these changes


1. While we wait on those results to be delivered, in the Codespace we can show the exploit in operation and why iti snot immediately obvious that we are vulnerable. Start the application by pressing `F5` to bring it up in the debugger.

   * The Codespace will detect the server running on port 8080, which you can then open in a browser window
      ![Screenshot 2022-06-26 at 16 48 25](https://user-images.githubusercontent.com/681306/175822510-8644faef-263d-44ed-8202-29ff6940b487.png)

   * Update the URL to add `/status` to the end of the current bookstore URL
      ![Screenshot 2022-06-26 at 16 49 31](https://user-images.githubusercontent.com/681306/175822561-06a955df-a0b9-454c-82b5-980b937ad997.png)

   * This is the status page used to inspect that the container is responding when we deploy it to an environment
      ![Screenshot 2022-06-26 at 16 50 25](https://user-images.githubusercontent.com/681306/175822606-dd35d5fd-3c0f-4d4b-87b8-c27586a4ee17.png)


1. Back in the Codespace, look at the terminal and see the log statement that have been output for the above browsing activity

   * The log statements all contain `api-version=` in them with no value (as the browser was not passing a header value for the API
      ![Screenshot 2022-06-26 at 16 52 19](https://user-images.githubusercontent.com/681306/175822688-8c157713-d8ba-4864-8fbc-68dd7ba2ad5b.png)
   

1. Now using another `Task: Run Task` called `curl: log4j injection attack` a terminal command will run with the following output

   ![Screenshot 2022-06-26 at 16 54 05](https://user-images.githubusercontent.com/681306/175822807-3bc48264-695b-4474-8bae-c420986741d7.png)

   ![Screenshot 2022-06-26 at 16 54 46](https://user-images.githubusercontent.com/681306/175822797-9a6b0e97-d2b0-4d72-8f1c-9e71ae66d683.png)


1. Return to the terminal that is being used for the Debug session of our application (either by closing the command terminal or selecting the `Debug: DemoServer` terminal from the other terminals in the list). In the outputs we will see in the injected JNDI lookup which if valid would mean that our application has just been compromised.

   ![Screenshot 2022-06-26 at 16 57 58](https://user-images.githubusercontent.com/681306/175822961-c277275c-7e19-4ec8-b895-1c0038e0dfca.png)

   
1. Now that we have shown the expolit off, the GitHub Actions workflows would have completed and we can now take a look at the findings... Return to the repository. Depending upon which branch you added the log4j code changes to, it will either be linked to a feature branch (you might need a PR to get all the workflows to run in this case) or if you committed directory to `main` as I did in these notes, the findings will be back up under the `Security` tab under `Code scanning alerts`


1. Exapand the `Tool` selector on the alert table header and you can see we have CodeQL findings of 1 and Grype (Anchor container scanner) has 14.

   ![Screenshot 2022-06-26 at 17 03 24](https://user-images.githubusercontent.com/681306/175823177-032f7b2a-2005-4938-9bea-1717bf012aed.png)

   * Tool selection of `CodeQL` detects the JNDI injection route via our CodeQL queries that we have active in our [code scanning workflow](../.github/workflows/code_scanning.yml#L34)
      ![Screenshot 2022-06-26 at 17 05 30](https://user-images.githubusercontent.com/681306/175823492-4ea16f55-dc3f-477f-b415-4e0395527769.png)
   
   * Tool selection of `Grype` will show all the container scan results, opening any one of them will give us the finding that we are using a vulnerable version of Log4j
      ![Screenshot 2022-06-26 at 17 13 36](https://user-images.githubusercontent.com/681306/175823565-f34db3a5-828e-441d-a3d4-a25da154029f.png)

   * We have managed to detect this vulnerability from two different routes by having two different tools in operation inside our workflows that report into GitHub Advanced Security alerts.
      One interesting aspect here is that dependabot did not detect this, and that is because we are not directly referencing Log4j and the vulnerable maven artifact, instead it is a transitive dependency of the `log4j-slf4j18-impl` which we use for logging with Jetty. This is an existing problem with package management systems that do not utilize lock files. Both Gradle and Maven complete dependency trees are not known until runtime (due to lack of local file and the complexities in how dependencies can be specified in these package management systems).
      The new (Dependency submission API)[https://docs.github.com/en/code-security/supply-chain-security/understanding-your-software-supply-chain/using-the-dependency-submission-api] will allow these tools to potentially fill in these gaps moving forward from the build time.
