package com.kinetise.data.systemdisplay.views;

import android.support.annotation.NonNull;
import android.view.View;

import com.kinetise.data.descriptors.*;
import com.kinetise.data.descriptors.actions.ActionVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.actions.functions.FunctionNextElementDataDesc;
import com.kinetise.data.descriptors.actions.functions.FunctionPreviousElementDataDesc;
import com.kinetise.data.descriptors.datadescriptors.*;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.scrolls.*;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewFactoryManager {

    private static Map<String, Class<? extends View>> customViewFactory = new HashMap<>();

    public static View createViewHierarchy(AbstractAGElementDataDesc desc, SystemDisplay display) {
        View view = createView(desc, display);
        if (desc instanceof AGScreenDataDesc) {
            createViewsForScreen((AGScreenView) view, (AGScreenDataDesc) desc, display);
        }
        if (desc instanceof IAGCollectionDataDesc) {
            createViewsForChildren((IAGCollectionView) view, (IAGCollectionDataDesc) desc, display);
        }

        return view;
    }

    private static void createViewsForChildren(IAGCollectionView parent, IAGCollectionDataDesc desc, SystemDisplay display) {
        List<AbstractAGElementDataDesc> controls = desc.getPresentControls();
        for (AbstractAGElementDataDesc control : controls) {
            if (control instanceof AbstractAGViewDataDesc && !((AbstractAGViewDataDesc) control).isHidden()) {
                IAGView view = (IAGView) createViewHierarchy(control, display);
                parent.addChildView(view);
            }
        }
    }

    private static void createViewsForScreen(AGScreenView view, AGScreenDataDesc desc, SystemDisplay display) {
        if (desc.getScreenHeader() != null) {
            AGHeaderView header = (AGHeaderView) createViewHierarchy(desc.getScreenHeader(), display);
            view.setSection(header);
        }
        if (desc.getScreenBody() != null) {
            AGBodyView body = (AGBodyView) createViewHierarchy(desc.getScreenBody(), display);
            view.setSection(body);
        }
        if (desc.getScreenNaviPanel() != null) {
            AGNaviPanelView naviPanel = (AGNaviPanelView) createViewHierarchy(desc.getScreenNaviPanel(), display);
            view.setSection(naviPanel);
        }
    }

    public static View createView(AbstractAGElementDataDesc desc, SystemDisplay display) {
        View newView;
        Class<? extends AbstractAGElementDataDesc> descClass = desc.getClass();
        if (descClass == AGScreenDataDesc.class) {
            newView = new AGScreenView(display, (AGScreenDataDesc) desc);
        } else if (descClass == AGBodyDataDesc.class) {
            newView = new AGBodyView(display, (AGBodyDataDesc) desc);
        } else if (descClass == AGNaviPanelDataDesc.class) {
            newView = new AGNaviPanelView(display, (AGNaviPanelDataDesc) desc);
        } else if (descClass == AGHeaderDataDesc.class) {
            newView = new AGHeaderView(display, (AGHeaderDataDesc) desc);
        } else if (descClass == AGTextInputDataDesc.class) {
            newView = new AGTextInputView(display, (AGTextInputDataDesc) desc);
        } else if (descClass == AGContainerHorizontalDataDesc.class) {
            newView = createContainer(display, (AGContainerHorizontalDataDesc) desc);
        } else if (descClass == AGRadioGroupHorizontalDataDesc.class) {
            newView = createContainer(display, (AGRadioGroupHorizontalDataDesc) desc);
        } else if (descClass == AGDataFeedHorizontalDataDesc.class) {
            newView = createContainer(display, (AGDataFeedHorizontalDataDesc) desc);
        } else if (descClass == AGContainerVerticalDataDesc.class) {
            newView = createContainer(display, (AGContainerVerticalDataDesc) desc);
        } else if (descClass == AGRadioGroupVerticalDataDesc.class) {
            newView = createContainer(display, (AGRadioGroupVerticalDataDesc) desc);
        } else if (descClass == AGDataFeedVerticalDataDesc.class) {
            newView = createContainer(display, (AGDataFeedVerticalDataDesc) desc);
        } else if (descClass == AGContainerTableDataDesc.class) {
            newView = createContainer(display, (AGContainerTableDataDesc) desc);
        } else if (descClass == AGRadioGroupTableDataDesc.class) {
            newView = createContainer(display, (AGRadioGroupTableDataDesc) desc);
        } else if (descClass == AGContainerThumbnailsDataDesc.class) {
            newView = createContainer(display, (AGContainerThumbnailsDataDesc) desc);
        } else if (descClass == AGRadioGroupThumbnailsDataDesc.class) {
            newView = createContainer(display, (AGRadioGroupThumbnailsDataDesc) desc);
        } else if (descClass == AGDataFeedThumbnailsDataDesc.class) {
            newView = createContainer(display, (AGDataFeedThumbnailsDataDesc) desc);
        } else if (descClass == AGRadioButtonDataDesc.class) {
            newView = new AGRadioButtonView(display, (AGRadioButtonDataDesc) desc);
        } else if (descClass == AGCheckBoxDataDesc.class) {
            newView = new AGCheckboxView(display, (AGCheckBoxDataDesc) desc);
        } else if (descClass == AGCodeScannerDataDesc.class) {
            newView = new AGCodeScannerView(display, (AGCodeScannerDataDesc) desc);
        } else if (descClass == AGGetPhoneContactDataDesc.class) {
            newView = new AGGetPhoneContactView(display, (AGGetPhoneContactDataDesc) desc);
        } else if (descClass == AGHyperlinkDataDesc.class) {
            newView = new AGHyperlinkView(display, (AGHyperlinkDataDesc) desc);
        } else if (descClass == AGDateDataDesc.class) {
            newView = new AGDateView(display, (AGDateDataDesc) desc);
        } else if (descClass == AGPasswordDataDesc.class) {
            newView = new AGPasswordView(display, (AGPasswordDataDesc) desc);
        } else if (descClass == AGSearchInputDataDesc.class) {
            newView = new AGSearchInputView(display, (AGSearchInputDataDesc) desc);
        } else if (descClass == AGPhotoDataDesc.class) {
            newView = new AGPhotoView(display, (AGPhotoDataDesc) desc);
        } else if (descClass == AGButtonDataDesc.class) {
            newView = createButton((AGButtonDataDesc) desc, display);
        } else if (descClass == AGLoadingDataDesc.class) {
            newView = new AGLoadingView(display, (AGLoadingDataDesc) desc);
        } else if (descClass == AGErrorDataDesc.class) {
            newView = new AGErrorView(display, (AGErrorDataDesc) desc);
        } else if (descClass == AGPinchImageDataDesc.class) {
            newView = new AGPinchImageView(display, (AGPinchImageDataDesc) desc);
        } else if (descClass == AGTextImageDataDesc.class) {
            newView = new AGTextImageView(display, (AGTextImageDataDesc) desc);
        } else if (descClass == AGTextDataDesc.class) {
            newView = new AGTextView(display, (AGTextDataDesc) desc);
        } else if (descClass == AGTextAreaDataDesc.class) {
            newView = new AGTextAreaView(display, (AGTextAreaDataDesc) desc);
        } else if (descClass == AGGalleryDataDesc.class) {
            newView = new AGGalleryView(display, (AGGalleryDataDesc) desc);
        } else if (descClass == AGWebBrowserDataDesc.class) {
            newView = new AGWebBrowserView(display, (AGWebBrowserDataDesc) desc);
        } else if (descClass == AGMapDataDesc.class) {
            newView = new AGMapView(display, (AGMapDataDesc) desc);
        } else if (descClass == AGVideoViewDataDesc.class) {
            newView = new AGVideoView(display, (AGVideoViewDataDesc) desc);
        } else if (descClass == AGDropdownDataDesc.class) {
            newView = new AGDropdownView(display, (AGDropdownDataDesc) desc);
        } else if (descClass == AGDatePickerDataDesc.class) {
            newView = new AGDatePickerView(display, (AGDatePickerDataDesc) desc);
        } else if (descClass == AGToggleButtonDataDesc.class) {
            newView = new AGToggleButtonView(display, (AGToggleButtonDataDesc) desc);
        } else if (descClass == AGChartDataDesc.class) {
            newView = new AGChartView(display, (AGChartDataDesc) desc);
        } else if (descClass == AGSignatureDataDesc.class) {
            newView = new AGSignatureView(display, (AGSignatureDataDesc) desc);
        } else if (descClass.isAssignableFrom(AGCustomControlDataDesc.class)) {
            newView = createCustomControlView((AGCustomControlDataDesc) desc, display);
        } else {
            throw new IllegalArgumentException(String.format("Unknown descriptor [%s], cannot create proper view",
                    desc.toString()));
        }

        if (desc instanceof AbstractAGViewDataDesc) {
            newView.setContentDescription(((AbstractAGViewDataDesc) desc).getId());
        }

        newView.setDrawingCacheEnabled(false);
        newView.setWillNotCacheDrawing(true);
        newView.setSoundEffectsEnabled(false);

        return newView;
    }

    @NonNull
    private static View createButton(AGButtonDataDesc desc, SystemDisplay display) {
        VariableDataDesc onClickActionDesc = desc.getOnClickActionDesc();
        if (onClickActionDesc != null && onClickActionDesc instanceof ActionVariableDataDesc) {
            ActionVariableDataDesc actionVariableDataDesc = (ActionVariableDataDesc) onClickActionDesc;
            if ((actionVariableDataDesc.getActions()).hasFunction(FunctionPreviousElementDataDesc.class)) {
                return new PreviousButtonView(display, desc);
            }
            if ((actionVariableDataDesc.getActions()).hasFunction(FunctionNextElementDataDesc.class)) {
                return new NextButtonView(display, desc);
            }
        }
        return new AGButtonView(display, desc);
    }

    private static AGContainerView createContainer(SystemDisplay display, AbstractAGContainerDataDesc containerDesc) {
        AGContainerView view;
        //W AbstractAGDataFeedDataDesc jest blokada na tworzenie widokow dla Vertical/Horizontal data feed jesli scrolluja sie tylko w kierunku ukladania dziei ze wzgledu na to
        //ze w ich przypadku widoki są tworzone przez adapter. W innym wypadku korzystamy ze standardowych dataFeedow wiec nie musimy tej funkcjonalnosci blokowac.
        //

        //TODO: page index field should be moved to data feed view class, but at the moment based on descriptor either DataFeedView or AGScrollView can be created
        //resetPageIndexForFeedDescriptor(containerDesc);

        //if (containerDesc instanceof AGDataFeedVerticalDataDesc && containerDesc.isScrollVertical() && !containerDesc.isScrollHorizontal()) {
        if (containerDesc instanceof AbstractAGRadioGroupDataDesc) {
            view = new AGRadioGroupView(display, (AbstractAGRadioGroupDataDesc) containerDesc);
        } else if (containerDesc instanceof AGDataFeedVerticalDataDesc && containerDesc.isScrollVertical() && !containerDesc.isScrollHorizontal()) {
            view = new DataFeedScrollView(display, containerDesc, ScrollType.VERTICAL);
        } else if (containerDesc instanceof AGDataFeedHorizontalDataDesc && containerDesc.isScrollHorizontal() && !containerDesc.isScrollVertical()) {
            view = new DataFeedScrollView(display, containerDesc, ScrollType.HORIZONTAL);
        } else if (containerDesc instanceof AbstractAGDataFeedDataDesc && containerDesc.isScrollHorizontal() && containerDesc.isScrollVertical()) {
            view = new DataFeedFreeScrollView(display, containerDesc);
        } else if (containerDesc.isScrollHorizontal() && containerDesc.isScrollVertical()) {
            view = new FreeScrollView(display, containerDesc);
        } else if (containerDesc.isScrollHorizontal()) {
            view = new AGScrollView(display, containerDesc, ScrollType.HORIZONTAL);
        } else if (containerDesc.isScrollVertical()) {
            view = new AGScrollView(display, containerDesc, ScrollType.VERTICAL);
        } else {
            view = new AGContainerView(display, containerDesc);
        }

        return view;
    }

    private static View createCustomControlView(AGCustomControlDataDesc desc, SystemDisplay display) {
        Class<? extends View> clazz = customViewFactory.get(desc.getControlName());
        try {
            return clazz.getConstructor(SystemDisplay.class, AGCustomControlDataDesc.class).newInstance(display, desc);
        } catch (Exception e) {
            e.printStackTrace();
            return new AGCustomControlView(display, desc);
        }
    }

    public static void registerCustomView(String controlName, Class<? extends View> customClass) {
        customViewFactory.put(controlName, customClass);
    }
}
