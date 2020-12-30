package com.taeheelee.eventmanagement;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = EventmanagementApplication.class)
public class DependencyTest {

	private static final String MODULES = "com.taeheelee.eventmanagement.modules..";
	private static final String ACCOUNT = "..modules.account..";
	private static final String ACTIVITY = "..modules.activity..";
	private static final String EVENT = "..modules.event..";
	private static final String MAIN = "..modules.main..";
	private static final String TAG = "..modules.tag..";
	private static final String ZONE = "..modules.zone..";
	
	@ArchTest
	ArchRule modulesRule = classes().that().resideInAPackage(MODULES)
	.should().onlyBeAccessed().byClassesThat().resideInAnyPackage(MODULES);
	
	@ArchTest
	ArchRule eventRule = classes().that().resideInAPackage(EVENT)
	.should().accessClassesThat().resideInAnyPackage(EVENT, ACTIVITY, MAIN);

	@ArchTest
	ArchRule activityRule = classes().that().resideInAPackage(ACTIVITY)
	.should().accessClassesThat().resideInAnyPackage(EVENT, ACCOUNT, ACTIVITY);
	
	@ArchTest
	ArchRule accountRule = classes().that().resideInAPackage(ACCOUNT)
	.should().accessClassesThat().resideInAnyPackage(TAG, ZONE, ACCOUNT);
	
	@ArchTest
	ArchRule checkCycle = slices().matching("com.taeheelee.eventmanagement.modules.(*)..")
	.should().beFreeOfCycles();
	
	
	
	
	
	
	
	
	
	
	
}
