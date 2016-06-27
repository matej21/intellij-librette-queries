package cz.matej21.intellij.librette.queries;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.NewExpression;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeAnalyserVisitor;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class QueriesTypeProvider implements PhpTypeProvider2 {

	final private static String SEPARATOR = "\u0280";
	final private static String TYPE_SEPARATOR = "\u0281";

	@Override
	public char getKey() {
		return '\u2109';
	}

	@Nullable
	@Override
	public String getType(PsiElement psiElement) {
		if (!(psiElement instanceof MethodReference)) {
			return null;
		}
		MethodReference reference = (MethodReference) psiElement;
		if (reference.getName() == null || !reference.getName().equals("fetch")) {
			return null;
		}
		if (reference.getParameters().length != 1) {
			return null;
		}
		if (isLocallyResolvableType(psiElement)) {
			return null;
		}
		PsiElement parameter = reference.getParameters()[0];
		if (!(parameter instanceof PhpTypedElement)) {
			return null;
		}
		PhpType type = ((PhpTypedElement) parameter).getType();
		String typeString = type.toString().replaceAll("\\|", TYPE_SEPARATOR);
		if (parameter instanceof NewExpression) {
			String entityName = resolveEntityName((NewExpression) parameter);
			if (!entityName.equals("")) {
				typeString += SEPARATOR + entityName;
			}
		}

		return typeString;
	}

	@Override
	public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
		String[] parts = s.split(SEPARATOR);
		PhpIndex index = PhpIndex.getInstance(project);
		Collection<PhpClass> classes = PhpIndexUtil.getByType(parts[0].split(TYPE_SEPARATOR), index);
		Collection<PhpNamedElement> result = new ArrayList<PhpNamedElement>(classes.size());
		for (PhpClass phpClass : classes) {
			if (phpClass.getFQN().equals("\\Librette\\Doctrine\\Queries\\EntityQuery")) {
				if (parts.length == 2) {
					result.addAll(PhpIndexUtil.getByType(parts[1].split(TYPE_SEPARATOR), index));
				}
			} else {
				Method method = phpClass.findMethodByName("fetch");
				if (method != null) {
					result.add(method);
				}
			}
		}
		return result;
	}

	private static boolean isLocallyResolvableType(PsiElement e) {
		PhpTypeAnalyserVisitor analyserVisitor = new PhpTypeAnalyserVisitor(0);
		e.accept(analyserVisitor);
		for (String type : analyserVisitor.getType().getTypes()) {
			if (!type.equals("?") && !type.startsWith("#")) {
				return true;
			}
		}
		return false;
	}

	private static String resolveEntityName(NewExpression expression) {
		if (expression.getParameters().length == 0) {
			return "";
		}
		PsiElement element = expression.getParameters()[0];
		if (!(element instanceof ClassConstantReference)) {
			return "";
		}
		ClassConstantReference reference = (ClassConstantReference) element;
		if (reference.getName() == null || !reference.getName().equals("class")) {
			return "";
		}
		if (reference.getClassReference() == null) {
			return "";
		}
		PhpType type = reference.getClassReference().getType();

		return type.toString().replaceAll("\\|", TYPE_SEPARATOR);
	}

}
