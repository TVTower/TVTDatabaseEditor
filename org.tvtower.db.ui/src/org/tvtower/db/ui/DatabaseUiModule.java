/*
 * generated by Xtext 2.23.0
 */
package org.tvtower.db.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.hover.IEObjectHover;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.outline.actions.OutlineWithEditorLinker;
import org.eclipse.xtext.ui.editor.toggleComments.DefaultSingleLineCommentHelper;
import org.eclipse.xtext.ui.validation.AbstractValidatorConfigurationBlock;
import org.tvtower.db.ui.hover.DatabaseEObjectHover;
import org.tvtower.db.ui.hover.DatabaseHoverProvider;
import org.tvtower.db.ui.outline.DatabaseOutlineWithEditorLinker;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used within the Eclipse IDE.
 */
public class DatabaseUiModule extends AbstractDatabaseUiModule {

	public DatabaseUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	public Class<? extends OutlineWithEditorLinker> bindOutlineLinker() {
		return DatabaseOutlineWithEditorLinker.class;
	}

	public Class<? extends IEObjectHoverProvider> bindHoverProvider() {
		return DatabaseHoverProvider.class;
	}

	@Override
	public Class<? extends IEObjectHover> bindIEObjectHover() {
		return DatabaseEObjectHover.class;
	}

	public Class<? extends AbstractValidatorConfigurationBlock> bindAbstractValidatorConfigurationBlock() {
		return DatabaseValidatorConfigurationBlock.class;
	}

	@Override
	public void configure(Binder binder) {
		super.configure(binder);
		// cause comment rule not to be found, so that no comment toggling is done
		binder.bind(String.class).annotatedWith(Names.named(DefaultSingleLineCommentHelper.SL_COMMENT))
				.toInstance("thereIsNoSLComment");
	}
}
