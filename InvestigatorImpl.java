package reflection.api;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public class InvestigatorImpl implements Investigator {
    private Class<?> theClass;
    private Object instance;
    @Override
    public void load(Object anInstanceOfSomething) {
        theClass=anInstanceOfSomething.getClass();
        instance=anInstanceOfSomething;
    }

    @Override
    public int getTotalNumberOfMethods() {
        try {
            Method[] theMethods;
            int numberOfMethods;

            theMethods = theClass.getDeclaredMethods();
            numberOfMethods= theMethods.length;

            return numberOfMethods;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public int getTotalNumberOfConstructors() {
        try {
            Constructor [] theConstructors;
            int numberOfConstructors;
            theConstructors=theClass.getDeclaredConstructors();
            numberOfConstructors=theConstructors.length;

            return numberOfConstructors;
        } catch (Exception e){
            throw e;
        }
    }

    @Override
    public int getTotalNumberOfFields() {
        try {
            int numberOfFields;
            Field [] theFields=theClass.getDeclaredFields();
            numberOfFields=theFields.length;

            return numberOfFields;
        } catch (Exception e){
            throw e;
        }
    }

    @Override
    public Set<String> getAllImplementedInterfaces() {
        Set<String> implementedInterfaces;
        try {
            implementedInterfaces=new HashSet<String>();
            Class<?>[] interfaces=theClass.getInterfaces();
            for(int i=0;i<interfaces.length;i++) {
                implementedInterfaces.add(interfaces[i].getSimpleName());
            }

            return implementedInterfaces;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public int getCountOfConstantFields() {
        try {
            int numberOfConstantsFields=0;
            Field[] theFields=theClass.getDeclaredFields();
            for (Field field: theFields) {
                if(Modifier.isFinal(field.getModifiers()))
                {
                    numberOfConstantsFields++;
                }
            }
            return numberOfConstantsFields;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public int getCountOfStaticMethods() {
        try {
            int numberOfStaticMethods=0;
            Method[] theMethods=theClass.getDeclaredMethods();
            for (Method method:theMethods) {
                if(Modifier.isStatic(method.getModifiers()))
                {
                    numberOfStaticMethods++;
                }
            }

            return numberOfStaticMethods;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean isExtending() {
        try {
            Class<?> parent = theClass.getSuperclass();
            return ((parent!=theClass)&&(parent!=Object.class));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public String getParentClassSimpleName() {
        try {
            String parentSimpleName=null;
            if(theClass.getSuperclass()!=Object.class)
            {
                parentSimpleName=theClass.getSuperclass().getSimpleName();
            }

            return parentSimpleName;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean isParentClassAbstract() {
        try {
            return (Modifier.isAbstract(theClass.getSuperclass().getModifiers()));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Set<String> getNamesOfAllFieldsIncludingInheritanceChain() {
        try {
            Set<String> namesOfAllFieldsInInheritanceChain=new HashSet<String>();
            Class<?> parent=theClass;
            Field [] fieldsOfClass;

            while(parent!=Object.class)
            {
                fieldsOfClass=parent.getDeclaredFields();

                for (Field field:fieldsOfClass) {
                    namesOfAllFieldsInInheritanceChain.add(field.getName());
                }
                parent=parent.getSuperclass();
            }

            return namesOfAllFieldsInInheritanceChain;
        } catch (SecurityException e) {
            throw e;
        }
    }

    @Override
    public int invokeMethodThatReturnsInt(String methodName, Object... args) {
        int returnValue=-1;
        boolean keepSearcing=true;
        try {
            Class[] classesOfArgs=new Class[args.length];
            Method[] allTheMethods=theClass.getDeclaredMethods();
            Method methodToInvoke=null;

            for (int i=0;i<allTheMethods.length&&keepSearcing;i++) {
                if(allTheMethods[i].getName()==methodName)
                {
                    methodToInvoke=allTheMethods[i];
                    keepSearcing=false;
                }
            }
            returnValue = (int)methodToInvoke.invoke(this.instance, args);
            return returnValue;
        }
        catch (Exception e) {
            return -1;
        }
    }

    @Override
    public Object createInstance(int numberOfArgs, Object... args) {
        Object instanceToReturn=null;
        boolean keepSearching=true;
        Parameter []allTheParameter=null;
        boolean parameterIsLegal=true;
        try {
            Constructor[] cons=theClass.getDeclaredConstructors();
            Constructor theWantedConstructor=null;

            for (int i = 0; i <cons.length &&keepSearching ; i++) {
                if (cons[i].getParameterCount()==numberOfArgs){
                    allTheParameter=cons[i].getParameters();
                    parameterIsLegal=true;
                    for (int j = 0; j <allTheParameter.length && parameterIsLegal; j++) {
                        if(allTheParameter[j].getType().getClass()!=args[j].getClass()) {
                            parameterIsLegal = false;
                            theWantedConstructor=cons[i];
                        }
                    }
                    if(parameterIsLegal)
                    {
                        keepSearching=false;
                    }
                }
            }

            instanceToReturn=theWantedConstructor.newInstance(args);

        }

         catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }

        return instanceToReturn;
    }

    @Override
    public Object elevateMethodAndInvoke(String name, Class<?>[] parametersTypes, Object... args) {
        Object objectToReturn=null;
        try {
            Method methodToInvoke=theClass.getDeclaredMethod(name, parametersTypes);
            methodToInvoke.setAccessible(true);
            objectToReturn=methodToInvoke.invoke(this.instance, args);
            return objectToReturn;
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }


    }

    @Override
    public String getInheritanceChain(String delimiter) {
        try {
            Class<?> currentClass=theClass;
            String stringOfInheritanceChain="";
            while(currentClass!=Object.class)
            {
                stringOfInheritanceChain=delimiter+currentClass.getSimpleName()+stringOfInheritanceChain;
                currentClass=currentClass.getSuperclass();
            }

            stringOfInheritanceChain=Object.class.getSimpleName()+stringOfInheritanceChain;
            return stringOfInheritanceChain;
        }

        catch (Exception e) {
            throw e;
        }
    }
}